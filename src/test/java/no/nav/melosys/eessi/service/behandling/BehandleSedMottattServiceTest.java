package no.nav.melosys.eessi.service.behandling;

import no.nav.eessi.basis.SedMottatt;
import no.nav.melosys.eessi.integration.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.service.joark.OpprettInngaaendeJournalpostService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BehandleSedMottattServiceTest {

    @Mock
    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    @Mock
    private AktoerConsumer aktoerConsumer;

    @InjectMocks
    private BehandleSedMottattService behandleSedMottattService;

    @Before
    public void setup() throws Exception {
        when(aktoerConsumer.getAktoerId(anyString()))
                .thenReturn("44332211");

        when(opprettInngaaendeJournalpostService.arkiverInngaaendeSed(any(), anyString()))
                .thenReturn("9988776655");
    }

    @Test
    public void behandleSed_expectServiceCalls() {
        SedMottatt sedMottatt = new SedMottatt();
        sedMottatt.setNavBruker("11223344");

        behandleSedMottattService.behandleSed(sedMottatt);
    }
}