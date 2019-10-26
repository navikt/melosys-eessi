package no.nav.melosys.eessi.service.joark;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import no.nav.melosys.eessi.service.sak.SakService;
import no.nav.melosys.eessi.service.tps.TpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpprettUtgaaendeJournalpostService {

    private final SakService sakService;
    private final JournalpostService journalpostService;
    private final EuxService euxService;
    private final TpsService tpsService;
    private final OppgaveService oppgaveService;

    @Autowired
    public OpprettUtgaaendeJournalpostService(
            SakService sakService, JournalpostService journalpostService, EuxService euxService,
            TpsService tpsService, OppgaveService oppgaveService) {
        this.journalpostService = journalpostService;
        this.sakService = sakService;
        this.euxService = euxService;
        this.tpsService = tpsService;
        this.oppgaveService = oppgaveService;
    }

    public String arkiverUtgaaendeSed(SedHendelse sedSendt) throws IntegrationException, NotFoundException {
        Optional<Sak> sak = sakService.finnSakForRinaSaksnummer(sedSendt.getRinaSakId());

        if (!sak.isPresent()) {
            return arkiverUtenSak(sedSendt);
        }

        return arkiverMedSak(sedSendt, sak.get());
    }

    private String arkiverMedSak(SedHendelse sedSendt, Sak sak) throws NotFoundException, IntegrationException {
        log.info("Journalfører dokument: {}", sedSendt.getRinaDokumentId());
        String navIdent = tpsService.hentNorskIdent(sak.getAktoerId());
        OpprettJournalpostResponse response = opprettUtgåendeJournalpost(sedSendt, sak, navIdent);

        if (!"ENDELIG".equalsIgnoreCase(response.getJournalstatus())) {
            log.info("Journalpost {} ble ikke endelig journalført. Melding: {}", response.getJournalpostId(), response.getMelding());
            opprettUtgåendeJournalføringsoppgave(sedSendt, response.getJournalpostId(), tpsService.hentAktoerId(navIdent));
        }

        return response.getJournalpostId();
    }

    private String arkiverUtenSak(SedHendelse sedSendt) throws IntegrationException, NotFoundException {
        log.info("Journalfører dokument uten sakstilknytning: {}", sedSendt.getRinaDokumentId());

        String navIdent = sedSendt.getNavBruker();
        OpprettJournalpostResponse response = opprettUtgåendeJournalpost(sedSendt, null, navIdent);
        opprettUtgåendeJournalføringsoppgave(sedSendt, response.getJournalpostId(), tpsService.hentAktoerId(navIdent));

        return response.getJournalpostId();
    }

    private OpprettJournalpostResponse opprettUtgåendeJournalpost(SedHendelse sedSendt, Sak sak, String navIdent) throws IntegrationException {
        return journalpostService.opprettUtgaaendeJournalpost(sedSendt, sak,
                euxService.hentSedMedVedlegg(sedSendt.getRinaSakId(), sedSendt.getRinaDokumentId()), navIdent);
    }

    private String opprettUtgåendeJournalføringsoppgave(SedHendelse sedSendt, String journalpostId, String navIdent) throws NotFoundException, IntegrationException {
        return oppgaveService.opprettUtgåendeJfrOppgave(journalpostId, sedSendt, tpsService.hentAktoerId(navIdent),
                euxService.hentRinaUrl(sedSendt.getRinaSakId()));
    }
}
