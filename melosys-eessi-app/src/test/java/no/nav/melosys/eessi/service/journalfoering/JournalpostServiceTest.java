package no.nav.melosys.eessi.service.journalfoering;

import java.nio.charset.Charset;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import no.nav.melosys.eessi.integration.journalpostapi.JournalpostapiConsumer;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostRequest;
import no.nav.melosys.eessi.integration.journalpostapi.OpprettJournalpostResponse;
import no.nav.melosys.eessi.integration.journalpostapi.SedAlleredeJournalførtException;
import no.nav.melosys.eessi.integration.sak.Sak;
import no.nav.melosys.eessi.kafka.consumers.SedHendelse;
import no.nav.melosys.eessi.models.vedlegg.SedMedVedlegg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JournalpostServiceTest {
    @Mock
    private JournalpostapiConsumer journalpostapiConsumer;
    @Mock
    private JournalpostMetadataService journalpostMetadataService;

    private final EnhancedRandom random = EnhancedRandomCreator.defaultEnhancedRandom();
    private final JournalpostMetadata journalpostMetadata = new JournalpostMetadata("dokumentTittel fra journalpostMetadata", "behandlingstema fra journalpostMetadata");

    private JournalpostService journalpostService;

    private SedHendelse sedHendelse;
    private Sak sak;
    private ObjectMapper objectMapper;
    private static final String JOURNALPOST_RESPONSE = "{\"journalpostId\":\"498371665\",\"journalstatus\":\"J\",\"melding\":null,\"dokumenter\":[{\"dokumentInfoId\":\"520426094\"}]}";

    @BeforeEach
    public void setUp() {
        journalpostService = new JournalpostService(journalpostMetadataService, journalpostapiConsumer);

        sedHendelse = random.nextObject(SedHendelse.class);
        sak = random.nextObject(Sak.class);
        objectMapper = new ObjectMapper();

        when(journalpostMetadataService.hentJournalpostMetadata(anyString())).thenReturn(journalpostMetadata);
    }

    @Test
    void opprettInngaaendeJournalpost_verifiserEndeligJfr() {
        journalpostService.opprettInngaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(new byte[0]), "123321");
        verify(journalpostapiConsumer).opprettJournalpost(any(OpprettJournalpostRequest.class), eq(false));
    }

    @Test
    void opprettUtgaaendeJournalpost_verifiserEndeligJfr() {
        journalpostService.opprettUtgaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(new byte[0]), "123321");
        verify(journalpostapiConsumer).opprettJournalpost(any(OpprettJournalpostRequest.class), eq(true));
    }

    @Test
    void opprettInngaaendeJournalpost_verifiserDokumentTittelOgBehandlingstema() {
        journalpostService.opprettInngaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(new byte[0]), "123321");
        var captor = ArgumentCaptor.forClass(OpprettJournalpostRequest.class);
        verify(journalpostapiConsumer).opprettJournalpost(captor.capture(), eq(false));
        assertThat(captor.getValue())
            .isNotNull()
            .extracting(OpprettJournalpostRequest::getTittel, OpprettJournalpostRequest::getBehandlingstema)
            .containsExactly(journalpostMetadata.dokumentTittel(), journalpostMetadata.behandlingstema());
    }

    @Test
    void opprettUtgaaendeJournalpost_verifiserDokumentTittelOgBehandlingstema() {
        journalpostService.opprettUtgaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(new byte[0]), "123321");
        var captor = ArgumentCaptor.forClass(OpprettJournalpostRequest.class);
        verify(journalpostapiConsumer).opprettJournalpost(captor.capture(), eq(true));
        assertThat(captor.getValue())
            .isNotNull()
            .extracting(OpprettJournalpostRequest::getTittel, OpprettJournalpostRequest::getBehandlingstema)
            .containsExactly(journalpostMetadata.dokumentTittel(), journalpostMetadata.behandlingstema());
    }

    @Test
    void opprettInngaaendeJournalpos_sedAlleredeJournalførtException_returnererOpprettJournalpostResponse() throws Exception {
        HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.CONFLICT, "", JOURNALPOST_RESPONSE.getBytes(), Charset.defaultCharset());
        when(journalpostapiConsumer.opprettJournalpost(any(OpprettJournalpostRequest.class), eq(false)))
            .thenThrow(new SedAlleredeJournalførtException("Sed allerede journalført", "123", httpClientErrorException));
        when(journalpostapiConsumer.henterJournalpostResponseFra409Exception(httpClientErrorException)).thenReturn(objectMapper.readValue(JOURNALPOST_RESPONSE, OpprettJournalpostResponse.class));

        OpprettJournalpostResponse opprettJournalpostResponse = journalpostService.opprettInngaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(new byte[0]), "123321");


        assertThat(opprettJournalpostResponse.getJournalpostId()).isEqualTo("498371665");
        verify(journalpostapiConsumer).opprettJournalpost(any(OpprettJournalpostRequest.class), eq(false));
    }

    private SedMedVedlegg sedMedVedlegg(byte[] innhold) {
        return new SedMedVedlegg(new SedMedVedlegg.BinaerFil("", "", innhold), Collections.emptyList());
    }
}
