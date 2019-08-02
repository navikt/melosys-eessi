package no.nav.melosys.eessi.service.joark;

import com.google.common.collect.Lists;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.service.gsak.GsakService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OpprettInngaaendeJournalpostServiceTest {

    @Mock
    private JournalpostService journalpostService;
    @Mock
    private GsakService gsakService;

    @InjectMocks
    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    private SedHendelse sedMottatt;

    @Before
    public void setup() throws Exception {
        sedMottatt = enhancedRandom.nextObject(SedHendelse.class);
        sedMottatt.setBucType(BucType.LA_BUC_01.name());

        OpprettJournalpostResponse response = new OpprettJournalpostResponse("11223344", Lists.newArrayList(
                new OpprettJournalpostResponse.Dokument("123")), null, null);
        when(journalpostService.opprettInngaaendeJournalpost(any(), any(), any())).thenReturn(response);

        Sak sak = enhancedRandom.nextObject(Sak.class);
        sak.setId("1234"); // Må kunne bli parset til Long
        when(gsakService.hentEllerOpprettSak(anyString(), anyString(), any()))
                .thenReturn(sak);
    }

    @Test
    public void arkiverInngaaendeSed_expectId() throws Exception {
        SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(sedMottatt, "123123", new byte[0]);

        assertThat(sakInformasjon, not(nullValue()));
        assertThat(sakInformasjon.getJournalpostId(), is("11223344"));

        verify(journalpostService, times(1)).opprettInngaaendeJournalpost(any(), any(), any());
        verify(gsakService, times(1)).hentEllerOpprettSak(any(), any(), any());
    }
}