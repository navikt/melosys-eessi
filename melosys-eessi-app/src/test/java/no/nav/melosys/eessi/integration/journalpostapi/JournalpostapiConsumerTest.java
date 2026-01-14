package no.nav.melosys.eessi.integration.journalpostapi;

import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class JournalpostapiConsumerTest {

    private static final String JOURNALPOST_RESPONSE = "{\"journalpostId\":\"498371665\",\"journalstatus\":\"J\",\"melding\":null,\"journalpostferdigstilt\":false,\"dokumenter\":[{\"dokumentInfoId\":\"520426094\"}]}";

    private MockRestServiceServer server;
    private RestTemplate restTemplate;
    private JsonMapper jsonMapper;
    private JournalpostapiConsumer journalpostapiConsumer;

    private final EnhancedRandom random = EnhancedRandomCreator.defaultEnhancedRandom();
    private OpprettJournalpostRequest request;

    @BeforeEach
    public void setUp() {
        restTemplate = new RestTemplate();
        jsonMapper = JsonMapper.builder().build();
        server = MockRestServiceServer.createServer(restTemplate);
        journalpostapiConsumer = new JournalpostapiConsumer(restTemplate, jsonMapper);
        request = random.nextObject(OpprettJournalpostRequest.class);
    }

    @Test
    void opprettJournalpost_skalJournalforeEndelig() {
        server.expect(requestTo("?forsoekFerdigstill=true"))
            .andRespond(withSuccess(JOURNALPOST_RESPONSE, MediaType.APPLICATION_JSON));

        journalpostapiConsumer.opprettJournalpost(request, true);
    }

    @Test
    void opprettJournalpost_skalIkkeJournalforeEndelig() {
        server.expect(requestTo("?forsoekFerdigstill=false"))
            .andRespond(withSuccess(JOURNALPOST_RESPONSE, MediaType.APPLICATION_JSON));
        journalpostapiConsumer.opprettJournalpost(request, false);
    }

    @Test
    void opprettJournalpost_eksternReferanseIdFinnesAlleredeHttp409_kasterException() {
        server.expect(requestTo("?forsoekFerdigstill=false")).andRespond(
            withStatus(HttpStatus.CONFLICT).body(JOURNALPOST_RESPONSE)
        );

        assertThatExceptionOfType(SedAlleredeJournalførtException.class)
            .isThrownBy(() -> journalpostapiConsumer.opprettJournalpost(request, false))
            .withMessageContaining("allerede journalført");
    }

    @Test
    void henterJournalpostResponseFra409Exception_ok() {
        server.expect(requestTo("?forsoekFerdigstill=false")).andRespond(
            withStatus(HttpStatus.CONFLICT).body(JOURNALPOST_RESPONSE)
        );

        try {
            journalpostapiConsumer.opprettJournalpost(request, false);
            fail("Skal kaste SedAlleredeJournalførtException");
        } catch (SedAlleredeJournalførtException sedAlleredeJournalførtException) {
            OpprettJournalpostResponse opprettJournalpostResponse = journalpostapiConsumer.henterJournalpostResponseFra409Exception(sedAlleredeJournalførtException.getEx());
            assertThat(opprettJournalpostResponse.getJournalpostId()).isEqualTo("498371665");
        }
    }
}
