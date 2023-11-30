package no.nav.melosys.eessi.service.joark;

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
import no.nav.melosys.eessi.service.dokkat.DokkatSedInfo;
import no.nav.melosys.eessi.service.dokkat.DokkatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private DokkatService dokkatService;
    @Mock
    private JournalpostapiConsumer journalpostapiConsumer;

    private JournalpostService journalpostService;

    private EnhancedRandom random = EnhancedRandomCreator.defaultEnhancedRandom();

    private SedHendelse sedHendelse;
    private Sak sak;
    private DokkatSedInfo dokkatSedInfo;
    private ObjectMapper objectMapper;
    private static final String JOURNALPOST_RESPONSE = "{\"journalpostId\":\"498371665\",\"journalstatus\":\"J\",\"melding\":null,\"dokumenter\":[{\"dokumentInfoId\":\"520426094\"}]}";

    @BeforeEach
    public void setUp() throws Exception {
        journalpostService = new JournalpostService(dokkatService, journalpostapiConsumer);

        sedHendelse = random.nextObject(SedHendelse.class);
        sak = random.nextObject(Sak.class);
        dokkatSedInfo = random.nextObject(DokkatSedInfo.class);
        objectMapper = new ObjectMapper();

        when(dokkatService.hentMetadataFraDokkat(anyString())).thenReturn(dokkatSedInfo);
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
    void opprettInngaaendeJournalpos_sedAlleredeJournalførtException_returnererOpprettJournalpostResponse() throws Exception{
        HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.CONFLICT, "", JOURNALPOST_RESPONSE.getBytes(), Charset.defaultCharset());
        when(journalpostapiConsumer.opprettJournalpost(any(OpprettJournalpostRequest.class), eq(false)))
            .thenThrow(new SedAlleredeJournalførtException("Sed allerede journalført", "123", httpClientErrorException));
        when(journalpostapiConsumer.henterJournalpostResponseFra409Exception(httpClientErrorException)).thenReturn(objectMapper.readValue(JOURNALPOST_RESPONSE, OpprettJournalpostResponse.class));

        OpprettJournalpostResponse opprettJournalpostResponse = journalpostService.opprettInngaaendeJournalpost(sedHendelse, sak, sedMedVedlegg(new byte[0]), "123321");


        assertThat(opprettJournalpostResponse.getJournalpostId()).isEqualTo("498371665");
        verify(journalpostapiConsumer).opprettJournalpost(any(OpprettJournalpostRequest.class), eq(false));
    }

    private SedMedVedlegg sedMedVedlegg(byte[] innhold) {
        return new SedMedVedlegg(new SedMedVedlegg.BinaerFil("","", innhold), Collections.emptyList());
    }
}
