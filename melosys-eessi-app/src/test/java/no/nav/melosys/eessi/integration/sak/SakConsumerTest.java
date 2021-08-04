package no.nav.melosys.eessi.integration.sak;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class SakConsumerTest {

    private SakConsumer sakConsumer;

    @Spy
    private RestTemplate restTemplate;

    private MockRestServiceServer server;

    @BeforeEach
    public void setup() {
        sakConsumer = new SakConsumer(restTemplate);
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getSak_expectSak() {
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
