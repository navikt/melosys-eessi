package no.nav.melosys.eessi.service.joark;

import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.journalpostapi.JournalpostapiConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import no.nav.melosys.eessi.service.dokkat.DokkatService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JournalpostServiceTest {

    @Mock
    private DokkatService dokkatService;
    @Mock
    private JournalpostapiConsumer journalpostapiConsumer;
    @InjectMocks
    private JournalpostService journalpostService;

    private EnhancedRandom random = EnhancedRandomCreator.defaultEnhancedRandom();

    private SedHendelse sedHendelse;
    private Sak sak;
    private DokkatSedInfo dokkatSedInfo;

    @Before
    public void setUp() throws Exception {
        sedHendelse = random.nextObject(SedHendelse.class);
        sak = random.nextObject(Sak.class);
        dokkatSedInfo = random.nextObject(DokkatSedInfo.class);

        when(dokkatService.hentMetadataFraDokkat(anyString())).thenReturn(dokkatSedInfo);
    }

    @Test
    public void opprettInngaaendeJournalpost_verifiserEndeligJfr() throws Exception {
        journalpostService.opprettInngaaendeJournalpost(sedHendelse, sak, new byte[0]);
        verify(journalpostapiConsumer).opprettJournalpost(any(OpprettJournalpostRequest.class), eq(false));
    }

    @Test
    public void opprettUtgaaendeJournalpost_verifiserEndeligJfr() throws Exception {
        journalpostService.opprettUtgaaendeJournalpost(sedHendelse, sak, new byte[0]);
        verify(journalpostapiConsumer).opprettJournalpost(any(OpprettJournalpostRequest.class), eq(true));
    }
}