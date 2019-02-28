package no.nav.melosys.eessi.service.behandling;

import lombok.extern.slf4j.Slf4j;
import no.nav.eessi.basis.SedMottatt;
import no.nav.melosys.eessi.integration.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BehandleSedMottattService {

    private final OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;
    private final AktoerConsumer aktoerConsumer;

    @Autowired
    public BehandleSedMottattService(OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService,
                                     AktoerConsumer aktoerConsumer) {
        this.opprettInngaaendeJournalpostService = opprettInngaaendeJournalpostService;
        this.aktoerConsumer = aktoerConsumer;
    }

    public void behandleSed(SedMottatt sedMottatt) {

        String aktoerId = aktoerConsumer.getAktoerId(sedMottatt.getNavBruker());

        try {
            String journalpostId = opprettInngaaendeJournalpostService.arkiverInngaaendeSed(sedMottatt, aktoerId);
            log.info("Midlertidig journalpost opprettet med id {}", journalpostId);
        } catch (IntegrationException e) {
            log.error("Sed ikke journalført: {}, melding: {}", sedMottatt, e.getMessage(), e);
        }
    }
}
