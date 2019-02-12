package no.nav.melosys.eessi.integration.gsak;

import no.nav.melosys.eessi.config.EnvironmentHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
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

        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("melosys.systemuser.username", "123");
        environment.setProperty("melosys.systemuser.password", "123");
        new EnvironmentHandler(environment);
    }

    @Test
    public void getSak_expectSak() throws Exception {
        String responseJson = "{\"id\":\"11\",\"tema\":\"MED\",\"applikasjon\":\"melsoys\","
                + "\"aktoerId\":\"123\",\"orgnr\":\"12312312\",\"fagsakNr\":\"fag123\","
                + "\"opprettetAv\":\"srvmelosys\",\"opprettetTidspunkt\":\"2019-02-11T08:33:38.964Z\"}";
        long sakId = 11L;
        server.expect(requestTo("/" + sakId))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        Sak response = sakConsumer.getSak(sakId);
        assertThat(response, not(nullValue()));
        assertThat(response.getId(), is(Long.toString(sakId)));

    }

}