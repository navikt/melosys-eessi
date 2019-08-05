package no.nav.melosys.eessi.integration.journalpostapi;

import io.github.benas.randombeans.api.EnhancedRandom;
import no.nav.melosys.eessi.EnhancedRandomCreator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class JournalpostapiConsumerTest {

    private MockRestServiceServer server;
    private RestTemplate restTemplate = new RestTemplate();

    private JournalpostapiConsumer journalpostapiConsumer;

    private EnhancedRandom random = EnhancedRandomCreator.defaultEnhancedRandom();
    private OpprettJournalpostRequest request;

    @Before
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

}