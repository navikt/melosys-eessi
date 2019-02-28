package no.nav.melosys.eessi.integration.dokmotinngaaende;

import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseRequest;
import no.nav.dok.tjenester.mottainngaaendeforsendelse.MottaInngaaendeForsendelseResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class DokmotInngaaendeConsumerTest {

    @Spy
    private RestTemplate restTemplate;

    private DokmotInngaaendeConsumer dokmotInngaaendeConsumer;

    private MockRestServiceServer server;

    private String responseJson;

    @Before
    public void setup() throws Exception {
        dokmotInngaaendeConsumer = new DokmotInngaaendeConsumer(restTemplate);
        server = MockRestServiceServer.createServer(restTemplate);

        URL responseJsonUrl = getClass().getClassLoader().getResource("mock/MottaInngaaendeForsendelseResponse.json");
        responseJson = new ObjectMapper().readTree(responseJsonUrl).toString();
    }

    @Test
    public void create() throws Exception {
        MottaInngaaendeForsendelseRequest request = new MottaInngaaendeForsendelseRequest();

        server.expect(requestTo("/mottaInngaaendeForsendelse"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        MottaInngaaendeForsendelseResponse response = dokmotInngaaendeConsumer.create(request);

        assertThat(response, not(nullValue()));
    }
}