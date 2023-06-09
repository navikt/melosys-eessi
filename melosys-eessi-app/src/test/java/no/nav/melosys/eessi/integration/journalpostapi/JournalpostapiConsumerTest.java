package no.nav.melosys.eessi.integration.journalpostapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest()
@AutoConfigureWebClient(registerRestTemplate = true)
class JournalpostapiConsumerTest {

    private static final String JOURNALPOST_RESPONSE = "{\"journalpostId\":\"498371665\",\"journalstatus\":\"J\",\"melding\":null,\"journalpostferdigstilt\":false,\"dokumenter\":[{\"dokumentInfoId\":\"520426094\"}]}";

    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private JournalpostapiConsumer journalpostapiConsumer;

    private final EnhancedRandom random = EnhancedRandomCreator.defaultEnhancedRandom();
    private OpprettJournalpostRequest request;

    @BeforeEach
    public void setUp() {
        journalpostapiConsumer = new JournalpostapiConsumer(restTemplate, objectMapper);
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
