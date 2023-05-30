package no.nav.melosys.eessi.service.joark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.PersonFasade;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.metrikker.SedMetrikker;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import no.nav.melosys.eessi.service.eux.EuxService;
import no.nav.melosys.eessi.service.oppgave.OppgaveService;
import no.nav.melosys.eessi.service.saksrelasjon.SaksrelasjonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpprettUtgaaendeJournalpostServiceTest {

    private static final String JOURNALPOST_ID = "123";

    @Mock
    private SaksrelasjonService saksrelasjonService;
    @Mock
    private JournalpostService journalpostService;
    @Mock
    private EuxService euxService;
    @Mock
    private PersonFasade personFasade;
    @Mock
    private OppgaveService oppgaveService;
    @Mock
    private SedMetrikker sedMetrikker;

    private OpprettUtgaaendeJournalpostService opprettUtgaaendeJournalpostService;

    private SedHendelse sedSendt;
    private final EnhancedRandom enhancedRandom = EnhancedRandomCreator.defaultEnhancedRandom();

    @BeforeEach
    public void setup() throws Exception {
        opprettUtgaaendeJournalpostService = new OpprettUtgaaendeJournalpostService(
            saksrelasjonService, journalpostService, euxService, personFasade, oppgaveService, sedMetrikker);

        when(euxService.hentSedMedVedlegg(anyString(), anyString())).thenReturn(sedMedVedlegg(new byte[0]));

        Sak sak = enhancedRandom.nextObject(Sak.class);
        when(saksrelasjonService.finnArkivsakForRinaSaksnummer(anyString())).thenReturn(Optional.of(sak));

        sedSendt = enhancedRandom.nextObject(SedHendelse.class);
    }

    @Test
    void arkiverUtgaaendeSed_forventId() {
        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, new ArrayList<>(), "ENDELIG", null);
        when(journalpostService.opprettUtgaaendeJournalpost(any(SedHendelse.class), any(), any(), any())).thenReturn(response);
        when(personFasade.hentNorskIdent(anyString())).thenReturn("54321");

        String result = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);
        assertThat(result).isEqualTo(JOURNALPOST_ID);
    }

    @Test
    void arkiverUtgaaendeSed_ikkeEndelig_forventOpprettJfrOppgave() {
        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, new ArrayList<>(), "MIDLERTIDIG", null);
        when(journalpostService.opprettUtgaaendeJournalpost(any(SedHendelse.class), any(), any(), any())).thenReturn(response);
        when(euxService.hentRinaUrl(anyString())).thenReturn("https://test.local");
        when(personFasade.hentAktoerId(anyString())).thenReturn("12345");
        when(personFasade.hentNorskIdent(anyString())).thenReturn("54321");

        String result = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);

        verify(saksrelasjonService).finnArkivsakForRinaSaksnummer(anyString());
        verify(journalpostService).opprettUtgaaendeJournalpost(any(), any(), any(), any());
        verify(oppgaveService).opprettUtgåendeJfrOppgave(anyString(), any(), anyString(), anyString());

        assertThat(result).isEqualTo(JOURNALPOST_ID);
    }

    @Test
    void arkiverUtgaaendeSed_ingenSak_forventOpprettJfrOppgave() {
        OpprettJournalpostResponse response = new OpprettJournalpostResponse(JOURNALPOST_ID, new ArrayList<>(), "ENDELIG", null);
        when(journalpostService.opprettUtgaaendeJournalpost(any(SedHendelse.class), any(), any(), any())).thenReturn(response);
        when(euxService.hentRinaUrl(anyString())).thenReturn("https://test.local");
        when(saksrelasjonService.finnArkivsakForRinaSaksnummer(anyString())).thenReturn(Optional.empty());
        when(personFasade.hentAktoerId(anyString())).thenReturn("12345");

        String journalpostId = opprettUtgaaendeJournalpostService.arkiverUtgaaendeSed(sedSendt);

        verify(saksrelasjonService).finnArkivsakForRinaSaksnummer(anyString());
        verify(journalpostService).opprettUtgaaendeJournalpost(any(), any(), any(), any());
        verify(oppgaveService).opprettUtgåendeJfrOppgave(anyString(), any(), anyString(), anyString());

        assertThat(journalpostId).isEqualTo(JOURNALPOST_ID);
    }

    private SedMedVedlegg sedMedVedlegg(byte[] innhold) {
        return new SedMedVedlegg(new SedMedVedlegg.BinaerFil("","", innhold), Collections.emptyList());
    }
}
