package no.nav.melosys.eessi.service.joark;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.MetrikkerRegistrering;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.gsak.GsakService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpprettUtgaaendeJournalpostService {

    private final GsakService gsakService;
    private final SaksrelasjonService saksrelasjonService;
    private final JournalpostService journalpostService;
    private final EuxService euxService;
    private final MetrikkerRegistrering metrikkerRegistrering;

    @Autowired
    public OpprettUtgaaendeJournalpostService(
            GsakService gsakService,
            SaksrelasjonService saksrelasjonService,
            JournalpostService journalpostService, EuxService euxService,
            MetrikkerRegistrering metrikkerRegistrering) {
        this.journalpostService = journalpostService;
        this.gsakService = gsakService;
        this.saksrelasjonService = saksrelasjonService;
        this.euxService = euxService;
        this.metrikkerRegistrering = metrikkerRegistrering;
    }

    public String arkiverUtgaaendeSed(SedHendelse sedSendt) throws IntegrationException, NotFoundException {

        Long gsakSaksnummer = saksrelasjonService.finnVedRinaId(sedSendt.getRinaSakId())
                .map(FagsakRinasakKobling::getGsakSaksnummer)
                .orElseThrow(() -> new NotFoundException("Saksrelasjon ikke funnet med rinaSakId " + sedSendt.getRinaSakId()));
        Sak sak = gsakService.hentsak(gsakSaksnummer);

        log.info("Journalfører dokument: {}", sedSendt.getRinaDokumentId());
        OpprettJournalpostResponse response = journalpostService.opprettUtgaaendeJournalpost(
                sedSendt, sak, euxService.hentSedPdf(sedSendt.getRinaSakId(), sedSendt.getRinaDokumentId()));

        metrikkerRegistrering.journalpostUtgaaendeOpprettet(response.erFerdigstilt());

        return response.getJournalpostId();
    }
}
