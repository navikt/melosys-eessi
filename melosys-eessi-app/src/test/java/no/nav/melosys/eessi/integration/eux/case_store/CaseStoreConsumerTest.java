package no.nav.melosys.eessi.integration.eux.case_store;

import java.util.Collections;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class CaseStoreConsumerTest {

    private CaseStoreConsumer caseStoreConsumer;

    private MockRestServiceServer server;
    private String response;

    @Before
    public void setup() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        caseStoreConsumer = new CaseStoreConsumer(restTemplate);
        response = new ObjectMapper().writeValueAsString(Collections.singletonList(new CaseStoreDto()));
    }

    @Test
    public void finnVedRinaSaksnummer() throws Exception {
        String rinaSaksnummer = "12312432";
        server.expect(requestTo("/cases?rinaId=" + rinaSaksnummer))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        caseStoreConsumer.finnVedRinaSaksnummer(rinaSaksnummer);
    }

    @Test
    public void finnVedJournalpostID() throws Exception {
        String journalpostID = "12312432";
        server.expect(requestTo("/cases?caseFileId=" + journalpostID))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        caseStoreConsumer.finnVedJournalpostID(journalpostID);
    }
}