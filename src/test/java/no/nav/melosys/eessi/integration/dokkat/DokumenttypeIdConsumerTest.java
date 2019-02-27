package no.nav.melosys.eessi.integration.dokkat;

import no.nav.dokkat.api.tkat022.DokumenttypeIdTo;
import no.nav.melosys.eessi.config.EnvironmentHandler;
import no.nav.melosys.eessi.models.exception.IntegrationException;
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
import static org.hamcrest.core.IsNot.not;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class DokumenttypeIdConsumerTest {


    @Spy
    private RestTemplate restTemplate;

    @InjectMocks
    private DokumenttypeIdConsumer dokumenttypeIdConsumer;

    private MockRestServiceServer server;

    @Before
    public void setup() {
        server = MockRestServiceServer.createServer(restTemplate);

        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("melosys.systemuser.username", "123");
        environment.setProperty("melosys.systemuser.password", "123");
        new EnvironmentHandler(environment);
    }

    @Test
    public void hentDokumenttypeId_expectValidJson() throws Exception {

        String responseJson = "{\"dokumenttypeId\":\"123\"}";
        server.expect(requestTo("/sed/type"))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        DokumenttypeIdTo dokumenttypeIdTo = dokumenttypeIdConsumer.hentDokumenttypeId("sed", "type");
        assertThat(dokumenttypeIdTo, not(nullValue()));
        assertThat(dokumenttypeIdTo.getDokumenttypeId(), is("123"));
    }

    @Test(expected = IntegrationException.class)
    public void hentDokumenttypeId_expectException() throws Exception {
        server.expect(requestTo("/sed/type"))
                .andRespond(withServerError());
        dokumenttypeIdConsumer.hentDokumenttypeId("sed", "type");
    }
}