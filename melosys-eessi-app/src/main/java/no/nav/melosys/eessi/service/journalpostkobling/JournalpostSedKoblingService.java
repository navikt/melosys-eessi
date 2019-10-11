package no.nav.melosys.eessi.service.journalpostkobling;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreConsumer;
import no.nav.melosys.eessi.integration.eux.case_store.CaseStoreDto;
import no.nav.melosys.eessi.kafka.producers.mapping.MelosysEessiMeldingMapperFactory;
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
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JournalpostSedKoblingService {

    private final JournalpostSedKoblingRepository journalpostSedKoblingRepository;
    private final CaseStoreConsumer caseStoreConsumer;
    private final EuxService euxService;
    private final SaksrelasjonService saksrelasjonService;

    public JournalpostSedKoblingService(
            JournalpostSedKoblingRepository journalpostSedKoblingRepository,
            CaseStoreConsumer caseStoreConsumer, EuxService euxService,
            SaksrelasjonService saksrelasjonService) {
        this.journalpostSedKoblingRepository = journalpostSedKoblingRepository;
        this.caseStoreConsumer = caseStoreConsumer;
        this.euxService = euxService;
        this.saksrelasjonService = saksrelasjonService;
    }

    public Optional<JournalpostSedKobling> finnVedJournalpostID(String journalpostID) {
        return journalpostSedKoblingRepository.findByJournalpostID(journalpostID);
    }

    public Optional<MelosysEessiMelding> finnVedJournalpostIDOpprettMelosysEessiMelding(String journalpostID)
            throws IntegrationException {
        Optional<JournalpostSedKobling> journalpostSedKobling = journalpostSedKoblingRepository.findByJournalpostID(journalpostID);
        if (journalpostSedKobling.isPresent()) {
            return opprettEessiMelding(journalpostSedKobling.get());
        }

        Optional<String> saksnummer = caseStoreConsumer.finnVedJournalpostID(journalpostID)
                .stream().findFirst().map(CaseStoreDto::getRinaSaksnummer);

        if (saksnummer.isPresent()) {
            return opprettEessiMelding(saksnummer.get(), journalpostID);
        }

        return Optional.empty();
    }

    private Optional<MelosysEessiMelding> opprettEessiMelding(JournalpostSedKobling journalpostSedKobling)
            throws IntegrationException {
        SED sed = euxService.hentSed(journalpostSedKobling.getRinaSaksnummer(), journalpostSedKobling.getSedId());

        Long gsakSaksnummer = saksrelasjonService.finnVedRinaSaksnummer(journalpostSedKobling.getRinaSaksnummer())
                .map(FagsakRinasakKobling::getGsakSaksnummer)
                .orElse(null);

        return Optional.of(opprettMelosysEessiMelding(sed, journalpostSedKobling.getSedId(), journalpostSedKobling.getRinaSaksnummer(),
                journalpostSedKobling.getSedType(), journalpostSedKobling.getBucType(), journalpostSedKobling.getJournalpostID(),
                gsakSaksnummer != null ? gsakSaksnummer.toString() : null, Integer.parseInt(journalpostSedKobling.getSedVersjon()) != 1
                ));
    }

    private Optional<MelosysEessiMelding> opprettEessiMelding(String rinaSaksnummer, String journalpostID) throws IntegrationException {
        BUC buc = euxService.hentBuc(rinaSaksnummer);
        Optional<Document> document = buc.hentSistOppdaterteDocument();
        if (!document.isPresent()) {
            log.warn("Finner ikke sist oppdaterte sed for rinasak {}",rinaSaksnummer);
            return Optional.empty();
        }
        String sedType = document.get().getType();
        String sedID = document.get().getId();
        SED sed = euxService.hentSed(rinaSaksnummer, sedID);
        String bucType = buc.getBucType();

        return Optional.of(opprettMelosysEessiMelding(sed, sedID, rinaSaksnummer, sedType, bucType, journalpostID, null, false));
    }

    public JournalpostSedKobling lagre(String journalpostID, String rinaSaksnummer, String sedID,
            String sedVersjon, String bucType, String sedType) {
        return journalpostSedKoblingRepository.save(
          new JournalpostSedKobling(journalpostID,rinaSaksnummer, sedID, sedVersjon, bucType, sedType)
        );
    }

    private MelosysEessiMelding opprettMelosysEessiMelding(SED sed, String sedId, String rinaSaksnummer, String sedType, String bucType, String journalpostID, String saksnummer, boolean erEndring) {
        return MelosysEessiMeldingMapperFactory.getMapper(SedType.valueOf(sedType))
                .map(null, sed, sedId, rinaSaksnummer, sedType, bucType, journalpostID, null, saksnummer, erEndring);
    }

}
