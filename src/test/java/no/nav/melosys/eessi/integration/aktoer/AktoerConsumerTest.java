package no.nav.melosys.eessi.integration.aktoer;

import no.nav.melosys.eessi.config.EnvironmentHandler;
import no.nav.melosys.eessi.integration.tps.aktoer.AktoerConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class AktoerConsumerTest {

    private static final String OK_RESPONSE =
            "{\"06038029973\":"
                    + "{\"identer\":"
                    + "[{\"ident\":\"1000004898116\","
                    + "\"identgruppe\":\"AktoerId\","
                    + "\"gjeldende\":true}],"
                    + "\"feilmelding\":null}}";

    private static final String FUNCTIONAL_ERROR_RESPONSE =
            "{\"12345678910\":"
                    + "{\"identer\":null,"
                    + "\"feilmelding\":\"Den angitte personidenten finnes ikke\"}}";

    @Spy
    private RestTemplate restTemplate;
    @InjectMocks
    private AktoerConsumer aktoerConsumer;

    private MockRestServiceServer server;


    @Before
    public void setUp() {
        server = MockRestServiceServer.createServer(restTemplate);
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("melosys.systemuser.username", "123");
        environment.setProperty("melosys.systemuser.password", "123");
        new EnvironmentHandler(environment);
    }

    @Test
    public void getAktoerIdOk() {
        server.expect(requestTo("/identer?identgruppe=AktoerId")).andRespond(withSuccess(OK_RESPONSE, MediaType.APPLICATION_JSON));
        assertThat(aktoerConsumer.getAktoerId("06038029973"), is("1000004898116"));
    }

    @Test
    public void getAktoerIdIdentFinnesIkke() {
        server.expect(requestTo("/identer?identgruppe=AktoerId"))
                .andRespond(withSuccess(FUNCTIONAL_ERROR_RESPONSE, MediaType.APPLICATION_JSON));
        assertThat(aktoerConsumer.getAktoerId("12345678910"), is(nullValue()));
    }
}
