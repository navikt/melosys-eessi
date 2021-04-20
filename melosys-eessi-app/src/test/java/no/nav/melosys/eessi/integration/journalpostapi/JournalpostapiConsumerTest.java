package no.nav.melosys.eessi.integration.journalpostapi;

import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class JournalpostapiConsumerTest {

    private static final String FEILMELDING_409_CONFLICT = "[{\"journalpostId\":\"498371665\",\"journalstatus\":\"J\",\"melding\":null,\"journalpostferdigstilt\":false,\"dokumenter\":[{\"dokumentInfoId\":\"520426094\"}]}]";

    private MockRestServiceServer server;
    private RestTemplate restTemplate = new RestTemplate();

    private JournalpostapiConsumer journalpostapiConsumer;

    private EnhancedRandom random = EnhancedRandomCreator.defaultEnhancedRandom();
    private OpprettJournalpostRequest request;

    @BeforeEach
    public void setUp() {
        journalpostapiConsumer = new JournalpostapiConsumer(restTemplate);
        server = MockRestServiceServer.createServer(restTemplate);
        request = random.nextObject(OpprettJournalpostRequest.class);
    }

    @Test
    public void opprettJournalpost_skalJournalforeEndelig() {
        server.expect(requestTo("?forsoekFerdigstill=true")).andRespond(withSuccess());
        journalpostapiConsumer.opprettJournalpost(request, true);
    }

    @Test
    public void opprettJournalpost_skalIkkeJournalforeEndelig() {
        server.expect(requestTo("?forsoekFerdigstill=false")).andRespond(withSuccess());
        journalpostapiConsumer.opprettJournalpost(request, false);
    }

    @Test
    public void opprettJournalpost_eksternReferanseIdFinnesAlleredeHttp409_kasterException() {
        server.expect(requestTo("?forsoekFerdigstill=false")).andRespond(
                withStatus(HttpStatus.CONFLICT).body(FEILMELDING_409_CONFLICT)
        );

        assertThatExceptionOfType(SedAlleredeJournalførtException.class)
                .isThrownBy(() -> journalpostapiConsumer.opprettJournalpost(request, false))
                .withMessageContaining("allerede journalført");
    }

}