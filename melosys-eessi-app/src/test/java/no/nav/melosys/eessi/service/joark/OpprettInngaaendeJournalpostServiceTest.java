package no.nav.melosys.eessi.service.joark;

import java.util.Optional;
import com.google.common.collect.Lists;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.gsak.Sak;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.MetrikkerRegistrering;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.service.gsak.GsakService;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OpprettInngaaendeJournalpostServiceTest {

    @Mock
    private JournalpostService journalpostService;
    @Mock
    private GsakService gsakService;
    @Mock
    private JournalpostSedKoblingService journalpostSedKoblingService;
    @Mock
    private MetrikkerRegistrering metrikkerRegistrering;

    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    private SedHendelse sedMottatt;
    private static final String JOURNALPOST_ID = "11223344";
    private static final String GSAK_SAKSNUMMER = "123";

    @Before
    public void setup() throws Exception {

        opprettInngaaendeJournalpostService = new OpprettInngaaendeJournalpostService(gsakService, journalpostService, journalpostSedKoblingService,
                metrikkerRegistrering);
        sedMottatt = enhancedRandom.nextObject(SedHendelse.class);
        sedMottatt.setBucType(BucType.LA_BUC_01.name());

        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, Lists.newArrayList(
                new OpprettJournalpostResponse.Dokument("123")), null, null);
        when(journalpostService.opprettInngaaendeJournalpost(any(), any(), any())).thenReturn(response);

        Sak sak = enhancedRandom.nextObject(Sak.class);
        sak.setId(GSAK_SAKSNUMMER);
        when(gsakService.finnSakForRinaID(anyString()))
                .thenReturn(Optional.of(sak));
    }

    @Test
    public void arkiverInngaaendeSedHentSakinformasjon_journalpostOpprettet_forventMottattJournalpostID() throws Exception {
        SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(sedMottatt, "123123", new byte[0]);

        assertThat(sakInformasjon).isNotNull();
        assertThat(sakInformasjon.getJournalpostId()).isEqualTo(JOURNALPOST_ID);
        assertThat(sakInformasjon.getGsakSaksnummer()).isEqualTo(GSAK_SAKSNUMMER);

        verify(journalpostService, times(1)).opprettInngaaendeJournalpost(any(), any(), any());
        verify(gsakService, times(1)).finnSakForRinaID(any());
        verify(journalpostSedKoblingService).lagre(eq(JOURNALPOST_ID), eq(sedMottatt.getRinaSakId()),
                eq(sedMottatt.getRinaDokumentId()), eq(sedMottatt.getRinaDokumentVersjon()),
                eq(sedMottatt.getBucType()), eq(sedMottatt.getSedType()));
    }

    @Test
    public void arkiverInngaaendeSedUtenBruker_journalpostOpprettet_forventReturnerJournalpostID() throws Exception {

        String journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(sedMottatt, new byte[0]);

        assertThat(journalpostID).isEqualTo(JOURNALPOST_ID);

        verify(journalpostSedKoblingService).lagre(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(journalpostService).opprettInngaaendeJournalpost(any(), isNull(), any());
    }
}