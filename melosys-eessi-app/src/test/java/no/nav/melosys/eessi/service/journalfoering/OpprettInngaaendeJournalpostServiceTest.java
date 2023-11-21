package no.nav.melosys.eessi.service.journalfoering;

import java.util.Collections;
import java.util.Optional;

import com.google.common.collect.Lists;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.BucType;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.journalpostkobling.JournalpostSedKoblingService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpprettInngaaendeJournalpostServiceTest {

    @Mock
    private JournalpostService journalpostService;
    @Mock
    private SaksrelasjonService saksrelasjonService;
    @Mock
    private JournalpostSedKoblingService journalpostSedKoblingService;

    private OpprettInngaaendeJournalpostService opprettInngaaendeJournalpostService;

    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    private SedHendelse sedMottatt;
    private static final String JOURNALPOST_ID = "11223344";
    private static final String GSAK_SAKSNUMMER = "123";

    @BeforeEach
    public void setup() throws Exception {

        opprettInngaaendeJournalpostService = new OpprettInngaaendeJournalpostService(saksrelasjonService, journalpostService, journalpostSedKoblingService);
        sedMottatt = enhancedRandom.nextObject(SedHendelse.class);
        sedMottatt.setBucType(BucType.LA_BUC_01.name());

        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, Lists.newArrayList(
                new OpprettJournalpostResponse.Dokument("123")), null, null);
        when(journalpostService.opprettInngaaendeJournalpost(any(), any(), any(), any())).thenReturn(response);
    }

    @Test
    void arkiverInngaaendeSedHentSakinformasjon_journalpostOpprettet_forventMottattJournalpostID() {
        var sak = enhancedRandom.nextObject(Sak.class);
        sak.setId(GSAK_SAKSNUMMER);
        when(saksrelasjonService.finnArkivsakForRinaSaksnummer(anyString())).thenReturn(Optional.of(sak));

        SakInformasjon sakInformasjon = opprettInngaaendeJournalpostService.arkiverInngaaendeSedHentSakinformasjon(sedMottatt, sedMedVedlegg(new byte[0]), "123");

        assertThat(sakInformasjon).isNotNull();
        assertThat(sakInformasjon.getJournalpostId()).isEqualTo(JOURNALPOST_ID);
        assertThat(sakInformasjon.getGsakSaksnummer()).isEqualTo(GSAK_SAKSNUMMER);

        verify(journalpostService, times(1)).opprettInngaaendeJournalpost(any(), any(), any(), anyString());
        verify(journalpostSedKoblingService).lagre(JOURNALPOST_ID, sedMottatt.getRinaSakId(),
                sedMottatt.getRinaDokumentId(), sedMottatt.getRinaDokumentVersjon(),
                sedMottatt.getBucType(), sedMottatt.getSedType());
    }

    @Test
    void arkiverInngaaendeSedUtenBruker_journalpostOpprettet_forventReturnerJournalpostID() {

        String journalpostID = opprettInngaaendeJournalpostService.arkiverInngaaendeSedUtenBruker(sedMottatt, sedMedVedlegg(new byte[0]), "123321");

        assertThat(journalpostID).isEqualTo(JOURNALPOST_ID);

        verify(journalpostSedKoblingService).lagre(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(journalpostService).opprettInngaaendeJournalpost(any(), isNull(), any(), anyString());
    }

    private SedMedVedlegg sedMedVedlegg(byte[] innhold) {
        return new SedMedVedlegg(new SedMedVedlegg.BinaerFil("","", innhold), Collections.emptyList());
    }
}
