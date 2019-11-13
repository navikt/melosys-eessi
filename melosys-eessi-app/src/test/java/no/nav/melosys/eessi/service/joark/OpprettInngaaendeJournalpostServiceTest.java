package no.nav.melosys.eessi.service.joark;

import java.util.Collections;
import java.util.Optional;
import com.google.common.collect.Lists;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.FagsakRinasakKobling;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import no.nav.melosys.eessi.service.sak.SakService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
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
    private SakService sakService;
    @Mock
    private SaksrelasjonService saksrelasjonService;
    @Mock
    private JournalpostSedKoblingService journalpostSedKoblingService;

    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    private SedHendelse sedMottatt;
    private static final String JOURNALPOST_ID = "11223344";
    private static final String GSAK_SAKSNUMMER = "123";

    @Before
    public void setup() throws Exception {

        opprettInngaaendeJournalpostService = new OpprettInngaaendeJournalpostService(sakService, saksrelasjonService, journalpostService, journalpostSedKoblingService);
        sedMottatt = enhancedRandom.nextObject(SedHendelse.class);
        sedMottatt.setBucType(BucType.LA_BUC_01.name());

        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, Lists.newArrayList(
                new OpprettJournalpostResponse.Dokument("123")), null, null);
        when(journalpostService.opprettInngaaendeJournalpost(any(), any(), any(), any())).thenReturn(response);

        Sak sak = enhancedRandom.nextObject(Sak.class);
        sak.setId(GSAK_SAKSNUMMER);
        when(sakService.hentsak(anyLong())).thenReturn(sak);
        FagsakRinasakKobling fagsakRinasakKobling = new FagsakRinasakKobling();
        fagsakRinasakKobling.setGsakSaksnummer(123L);
        when(saksrelasjonService.finnVedRinaSaksnummer(anyString())).thenReturn(Optional.of(fagsakRinasakKobling));
    }

    @Test
    public void arkiverInngaaendeSedHentSakinformasjon_journalpostOpprettet_forventMottattJournalpostID() throws Exception {
        SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(sedMottatt, sedMedVedlegg(new byte[0]), "123");

        assertThat(sakInformasjon).isNotNull();
        assertThat(sakInformasjon.getJournalpostId()).isEqualTo(JOURNALPOST_ID);
        assertThat(sakInformasjon.getGsakSaksnummer()).isEqualTo(GSAK_SAKSNUMMER);

        verify(journalpostService, times(1)).opprettInngaaendeJournalpost(any(), any(), any(), anyString());
        verify(saksrelasjonService, times(1)).finnVedRinaSaksnummer(any());
        verify(journalpostSedKoblingService).lagre(eq(JOURNALPOST_ID), eq(sedMottatt.getRinaSakId()),
                eq(sedMottatt.getRinaDokumentId()), eq(sedMottatt.getRinaDokumentVersjon()),
                eq(sedMottatt.getBucType()), eq(sedMottatt.getSedType()));
    }

    @Test
    public void arkiverInngaaendeSedUtenBruker_journalpostOpprettet_forventReturnerJournalpostID() throws Exception {

        String journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(sedMottatt, sedMedVedlegg(new byte[0]), "123321");

        assertThat(journalpostID).isEqualTo(JOURNALPOST_ID);

        verify(journalpostSedKoblingService).lagre(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(journalpostService).opprettInngaaendeJournalpost(any(), isNull(), any(), anyString());
    }

    private SedMedVedlegg sedMedVedlegg(byte[] innhold) {
        return new SedMedVedlegg(new SedMedVedlegg.BinaerFil("","", innhold), Collections.emptyList());
    }
}