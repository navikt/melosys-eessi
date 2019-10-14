package no.nav.melosys.eessi.integration.sak;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class SakConsumerTest {

    private SakConsumer sakConsumer;

    @Spy
    private RestTemplate restTemplate;

    private MockRestServiceServer server;

    @Before
    public void setup() {
        sakConsumer = new SakConsumer(restTemplate);
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void getSak_expectSak() throws Exception {
        String responseJson = "{\"id\":\"11\",\"tema\":\"MED\",\"applikasjon\":\"melsoys\","
                + "\"aktoerId\":\"123\",\"orgnr\":\"12312312\",\"fagsakNr\":\"fag123\","
                + "\"opprettetAv\":\"srvmelosys\",\"opprettetTidspunkt\":\"2019-02-11T08:33:38.964Z\"}";
        String sakId = "11";
        server.expect(requestTo("/" + sakId))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        Sak response = sakConsumer.getSak(sakId);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(sakId);

    }
}