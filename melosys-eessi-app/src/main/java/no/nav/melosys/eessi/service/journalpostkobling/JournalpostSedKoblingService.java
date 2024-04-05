package no.nav.melosys.eessi.service.journalpostkobling;

import java.util.Optional;

import io.getunleash.Unleash;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.config.featuretoggle.ToggleName;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreConsumer;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreDto;
import no.nav.melosys.eessi.integration.saf.SafConsumer;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.JournalpostSedKobling;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.repository.JournalpostSedKoblingRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JournalpostSedKoblingService {

    private final JournalpostSedKoblingRepository journalpostSedKoblingRepository;
    private final CaseStoreConsumer caseStoreConsumer;
    private final EuxService euxService;
    private final SaksrelasjonService saksrelasjonService;
    private final SafConsumer safConsumer;
    private final MelosysEessiMeldingMapperFactory melosysEessiMeldingMapperFactory;
    private final Unleash unleash;

    public JournalpostSedKoblingService(
            JournalpostSedKoblingRepository journalpostSedKoblingRepository,
            CaseStoreConsumer caseStoreConsumer,
            EuxService euxService,
            SaksrelasjonService saksrelasjonService,
            SafConsumer safConsumer,
            MelosysEessiMeldingMapperFactory melosysEessiMeldingMapperFactory, Unleash unleash) {
        this.journalpostSedKoblingRepository = journalpostSedKoblingRepository;
        this.caseStoreConsumer = caseStoreConsumer;
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
        this.safConsumer = safConsumer;
        this.melosysEessiMeldingMapperFactory = melosysEessiMeldingMapperFactory;
        this.unleash = unleash;
    }

    public Optional<JournalpostSedKobling> finnVedJournalpostID(String journalpostID) {
        return journalpostSedKoblingRepository.findByJournalpostID(journalpostID);
    }

    public Optional<MelosysEessiMelding> finnVedJournalpostIDOpprettMelosysEessiMelding(String journalpostID) {
        Optional<JournalpostSedKobling> journalpostSedKobling = journalpostSedKoblingRepository
                .findByJournalpostID(journalpostID);

        if (journalpostSedKobling.isPresent()) {
            return Optional.of(opprettEessiMelding(journalpostSedKobling.get()));
        }

        Optional<String> rinaSaksnummer = søkEtterRinaSaksnummerForJournalpost(journalpostID);

        if (rinaSaksnummer.isPresent()) {
            return opprettEessiMelding(rinaSaksnummer.get(), journalpostID);
        }

        return Optional.empty();
    }

    public boolean erASedAlleredeBehandlet(String rinaSaksnummer){
        return journalpostSedKoblingRepository.findByRinaSaksnummer(rinaSaksnummer).stream()
            .anyMatch(JournalpostSedKobling::erASed);
    }

    private Optional<String> søkEtterRinaSaksnummerForJournalpost(String journalpostID) {
        Optional<String> rinaSaksnummer = safConsumer.hentRinasakForJournalpost(journalpostID);
        if (rinaSaksnummer.isEmpty()) {
            log.info("Rinasaksnummer er null fra saf for journalpostId: {}", journalpostID);
        }

        return rinaSaksnummer.isPresent() ? rinaSaksnummer : hentFraCaseStore(journalpostID);
    }

    private Optional<String> hentFraCaseStore(String journalpostID) {
        if(unleash.isEnabled(ToggleName.IKKE_HENT_FRA_CASESTORE)) {
            log.info("søkEtterRinaSaksnummerForJournalpost: henting fra casestore er togglet av");
            return Optional.empty();
        }

        return caseStoreConsumer.finnVedJournalpostID(journalpostID)
            .stream().findFirst().map(CaseStoreDto::getRinaSaksnummer);
    }

    private MelosysEessiMelding opprettEessiMelding(JournalpostSedKobling journalpostSedKobling) {
        BUC buc = euxService.hentBuc(journalpostSedKobling.getRinaSaksnummer());
        final var organisation = buc.hentDokument(journalpostSedKobling.getSedId()).getCreator().getOrganisation();
        SED sed = euxService.hentSed(journalpostSedKobling.getRinaSaksnummer(), journalpostSedKobling.getSedId());

        Long gsakSaksnummer = saksrelasjonService.finnVedRinaSaksnummer(journalpostSedKobling.getRinaSaksnummer())
                .map(FagsakRinasakKobling::getGsakSaksnummer)
                .orElse(null);

        return opprettMelosysEessiMelding(
                sed,
                journalpostSedKobling.getSedId(),
                journalpostSedKobling.getRinaSaksnummer(),
                journalpostSedKobling.getSedType(),
                journalpostSedKobling.getBucType(),
                organisation.getId(),
                organisation.getCountryCode(),
                journalpostSedKobling.getJournalpostID(),
                gsakSaksnummer != null ? gsakSaksnummer.toString() : null,
                Integer.parseInt(journalpostSedKobling.getSedVersjon()) != 1,
                journalpostSedKobling.getSedVersjon()
        );
    }

    private Optional<MelosysEessiMelding> opprettEessiMelding(String rinaSaksnummer, String journalpostID) {
        BUC buc = euxService.hentBuc(rinaSaksnummer);
        Optional<Document> documentOptional = buc.hentSistOppdaterteDocument();
        if (documentOptional.isEmpty()) {
            log.warn("Finner ikke sist oppdaterte sed for rinasak {}", rinaSaksnummer);
            return Optional.empty();
        }
        final Document sedDocument = documentOptional.get();
        String sedID = sedDocument.getId();
        String sedType = sedDocument.getType();
        final var organisation = sedDocument.getCreator().getOrganisation();
        SED sed = euxService.hentSed(rinaSaksnummer, sedID);
        final String sedVersjon = "0"; //har ikke sed-versjon

        return Optional.of(opprettMelosysEessiMelding(sed, sedID, rinaSaksnummer, sedType,
                buc.getBucType(), organisation.getId(), organisation.getCountryCode(), journalpostID, null, false, sedVersjon));
    }

    public JournalpostSedKobling lagre(String journalpostID, String rinaSaksnummer, String sedID,
            String sedVersjon, String bucType, String sedType) {
        return journalpostSedKoblingRepository.save(
                new JournalpostSedKobling(journalpostID, rinaSaksnummer, sedID, sedVersjon, bucType, sedType)
        );
    }

    private MelosysEessiMelding opprettMelosysEessiMelding(SED sed, String sedId, String rinaSaksnummer,
                                                           String sedType, String bucType, String avsenderID, String landkode,
                                                           String journalpostID, String saksnummer, boolean erEndring, String sedVersjon) {
        return melosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(sedType))
                .map(null, sed, sedId, rinaSaksnummer, sedType, bucType, avsenderID, landkode, journalpostID,
                        null, saksnummer, erEndring, sedVersjon);
    }
}
