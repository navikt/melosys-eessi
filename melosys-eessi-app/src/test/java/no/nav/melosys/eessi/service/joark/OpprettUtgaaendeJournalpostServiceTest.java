package no.nav.melosys.eessi.service.joark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import no.nav.melosys.eessi.service.sak.SakService;
import no.nav.melosys.eessi.service.tps.TpsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OpprettUtgaaendeJournalpostServiceTest {

    private static final String JOURNALPOST_ID = "123";

    @Mock
    private SakService sakService;
    @Mock
    private JournalpostService journalpostService;
    @Mock
    private EuxService euxService;
    @Mock
    private TpsService tpsService;
    @Mock
    private OppgaveService oppgaveService;

    private OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;

    private SedHendelse sedSendt;
    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    @Before
    public void setup() throws Exception {
        opprettUtgaaendeJournalpostService = new OpprettUtgaaendeJournalpostService(
                sakService, journalpostService, euxService, tpsService, oppgaveService);

        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, new ArrayList<>(), "ENDELIG", null);
        when(journalpostService.opprettUtgaaendeJournalpost(any(SedHendelse.class), any(), any(), any())).thenReturn(response);
        when(euxService.hentSedMedVedlegg(anyString(), anyString())).thenReturn(sedMedVedlegg(new byte[0]));
        when(euxService.hentRinaUrl(anyString())).thenReturn("https://test.local");
        when(tpsService.hentNorskIdent(anyString())).thenReturn("54321");
        when(tpsService.hentAktoerId(anyString())).thenReturn("12345");

        Sak sak = enhancedRandom.nextObject(Sak.class);
        when(sakService.finnSakForRinaSaksnummer(anyString())).thenReturn(Optional.of(sak));

        sedSendt = enhancedRandom.nextObject(SedHendelse.class);
    }

    @Test
    public void arkiverUtgaaendeSed_forventId() throws Exception {
        String result = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);
        assertThat(result).isEqualTo(JOURNALPOST_ID);
    }

    @Test
    public void arkiverUtgaaendeSed_ikkeEndelig_forventOpprettJfrOppgave() throws NotFoundException, IntegrationException {
        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, new ArrayList<>(), "MIDLERTIDIG", null);
        when(journalpostService.opprettUtgaaendeJournalpost(any(SedHendelse.class), any(), any(), any())).thenReturn(response);

        String result = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);

        verify(sakService).finnSakForRinaSaksnummer(anyString());
        verify(journalpostService).opprettUtgaaendeJournalpost(any(), any(), any(), any());
        verify(oppgaveService).opprettUtgåendeJfrOppgave(anyString(), any(), anyString(), anyString());

        assertThat(result).isEqualTo(JOURNALPOST_ID);
    }

    @Test
    public void arkiverUtgaaendeSed_ingenSak_forventOpprettJfrOppgave() throws Exception {
        when(sakService.finnSakForRinaSaksnummer(anyString())).thenReturn(Optional.empty());

        String journalpostId = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);

        verify(sakService).finnSakForRinaSaksnummer(anyString());
        verify(journalpostService).opprettUtgaaendeJournalpost(any(), any(), any(), any());
        verify(oppgaveService).opprettUtgåendeJfrOppgave(anyString(), any(), anyString(), anyString());

        assertThat(journalpostId).isEqualTo(JOURNALPOST_ID);
    }

    private SedMedVedlegg sedMedVedlegg(byte[] innhold) {
        return new SedMedVedlegg(new SedMedVedlegg.BinaerFil("","", innhold), Collections.emptyList());
    }
}
