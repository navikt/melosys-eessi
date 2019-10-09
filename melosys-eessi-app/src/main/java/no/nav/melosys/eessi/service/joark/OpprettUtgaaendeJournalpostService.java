package no.nav.melosys.eessi.service.joark;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.sak.SakService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpprettUtgaaendeJournalpostService {

    private final SakService sakService;
    private final SaksrelasjonService saksrelasjonService;
    private final JournalpostService journalpostService;
    private final EuxService euxService;

    @Autowired
    public OpprettUtgaaendeJournalpostService(
            SakService sakService,
            SaksrelasjonService saksrelasjonService,
            JournalpostService journalpostService, EuxService euxService) {
        this.journalpostService = journalpostService;
        this.sakService = sakService;
        this.saksrelasjonService = saksrelasjonService;
        this.euxService = euxService;
    }

    public String arkiverUtgaaendeSed(SedHendelse sedSendt) throws IntegrationException, NotFoundException {

        Long gsakSaksnummer = saksrelasjonService.finnVedRinaId(sedSendt.getRinaSakId())
                .map(FagsakRinasakKobling::getGsakSaksnummer)
                .orElseThrow(() -> new NotFoundException("Saksrelasjon ikke funnet med rinaSakId " + sedSendt.getRinaSakId()));
        Sak sak = sakService.hentsak(gsakSaksnummer);

        log.info("Journalfører dokument: {}", sedSendt.getRinaDokumentId());
        OpprettJournalpostResponse response = journalpostService.opprettUtgaaendeJournalpost(
                sedSendt, sak, euxService.hentSedMedVedlegg(sedSendt.getRinaSakId(), sedSendt.getRinaDokumentId()));

        return response.getJournalpostId();
    }
}
