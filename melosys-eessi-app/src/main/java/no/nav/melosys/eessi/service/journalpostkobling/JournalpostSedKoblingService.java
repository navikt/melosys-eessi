package no.nav.melosys.eessi.service.journalpostkobling;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreConsumer;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreDto;
import no.nav.melosys.eessi.integration.saf.SafConsumer;
import no.nav.melosys.eessi.kafka.producers.model.MelosysEessiMelding;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.JournalpostSedKobling;
import no.nav.melosys.eessi.models.SedType;
import no.nav.melosys.eessi.models.buc.BUC;
import no.nav.melosys.eessi.models.buc.Document;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.sed.SED;
import no.nav.melosys.eessi.repository.JournalpostSedKoblingRepository;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import no.nav.melosys.eessi.service.sed.mapper.fra_sed.melosys_eessi_melding.MelosysEessiMeldingMapperFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JournalpostSedKoblingService {

    private final JournalpostSedKoblingRepository journalpostSedKoblingRepository;
    private final CaseStoreConsumer caseStoreConsumer;
    private final EuxService euxService;
    private final SaksrelasjonService saksrelasjonService;
    private final SafConsumer safConsumer;

    public JournalpostSedKoblingService(
            JournalpostSedKoblingRepository journalpostSedKoblingRepository,
            CaseStoreConsumer caseStoreConsumer, EuxService euxService,
            SaksrelasjonService saksrelasjonService, SafConsumer safConsumer) {
        this.journalpostSedKoblingRepository = journalpostSedKoblingRepository;
        this.caseStoreConsumer = caseStoreConsumer;
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
        this.safConsumer = safConsumer;
    }

    public Optional<JournalpostSedKobling> finnVedJournalpostID(String journalpostID) {
        return journalpostSedKoblingRepository.findByJournalpostID(journalpostID);
    }

    public Optional<MelosysEessiMelding> finnVedJournalpostIDOpprettMelosysEessiMelding(String journalpostID)
            throws IntegrationException {
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

    private Optional<String> søkEtterRinaSaksnummerForJournalpost(String journalpostID) throws IntegrationException {
        Optional<String> rinaSaksnummer = safConsumer.hentRinasakForJournalpost(journalpostID);

        return rinaSaksnummer.isPresent() ? rinaSaksnummer : caseStoreConsumer.finnVedJournalpostID(journalpostID)
                .stream().findFirst().map(CaseStoreDto::getRinaSaksnummer);
    }

    private MelosysEessiMelding opprettEessiMelding(JournalpostSedKobling journalpostSedKobling)
            throws IntegrationException {
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
                journalpostSedKobling.getJournalpostID(),
                gsakSaksnummer != null ? gsakSaksnummer.toString() : null,
                Integer.parseInt(journalpostSedKobling.getSedVersjon()) != 1
        );
    }

    private Optional<MelosysEessiMelding> opprettEessiMelding(String rinaSaksnummer, String journalpostID)
            throws IntegrationException {
        BUC buc = euxService.hentBuc(rinaSaksnummer);
        Optional<Document> document = buc.hentSistOppdaterteDocument();
        if (document.isEmpty()) {
            log.warn("Finner ikke sist oppdaterte sed for rinasak {}", rinaSaksnummer);
            return Optional.empty();
        }
        String sedType = document.get().getType();
        String sedID = document.get().getId();
        SED sed = euxService.hentSed(rinaSaksnummer, sedID);
        String bucType = buc.getBucType();

        return Optional.of(opprettMelosysEessiMelding(sed, sedID, rinaSaksnummer, sedType, bucType, journalpostID, null,
                false));
    }

    public JournalpostSedKobling lagre(String journalpostID, String rinaSaksnummer, String sedID,
            String sedVersjon, String bucType, String sedType) {
        return journalpostSedKoblingRepository.save(
                new JournalpostSedKobling(journalpostID, rinaSaksnummer, sedID, sedVersjon, bucType, sedType)
        );
    }

    private MelosysEessiMelding opprettMelosysEessiMelding(SED sed, String sedId, String rinaSaksnummer, String sedType,
            String bucType, String journalpostID, String saksnummer, boolean erEndring) {
        return MelosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(sedType))
                .map(null, sed, sedId, rinaSaksnummer, sedType, bucType, journalpostID, null, saksnummer, erEndring);
    }
}
