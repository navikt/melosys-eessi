package no.nav.melosys.eessi.integration.aktoer;

import no.nav.melosys.eessi.integration.tps.aktoer.AktoerConsumer;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class AktoerConsumerTest {

    private static final String OK_RESPONSE_AKTOER =
            "{\"06038029973\":"
                    + "{\"identer\":"
                    + "[{\"ident\":\"1000004898116\","
                    + "\"identgruppe\":\"AktoerId\","
                    + "\"gjeldende\":true}],"
                    + "\"feilmelding\":null}}";

    private static final String OK_RESPONSE_NORSK_IDENT =
            "{\"06038029973\":"
                    + "{\"identer\":"
                    + "[{\"ident\":\"1000004898117\","
                    + "\"identgruppe\":\"NorskIdent\","
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
    }

    @Test
    public void hentAktoerIdOk() throws Exception {
        server.expect(requestTo("/identer?identgruppe=AktoerId")).andRespond(withSuccess(OK_RESPONSE_AKTOER, MediaType.APPLICATION_JSON));
        assertThat(aktoerConsumer.hentAktoerId("06038029973"), is("1000004898116"));
    }

    @Test
    public void hentNorskIdent() throws Exception {
        server.expect(requestTo("/identer?identgruppe=NorskIdent")).andRespond(withSuccess(OK_RESPONSE_NORSK_IDENT, MediaType.APPLICATION_JSON));
        assertThat(aktoerConsumer.hentNorskIdent("06038029973"), is("1000004898117"));
    }

    @Test(expected = NotFoundException.class)
    public void getAktoerIdIdentFinnesIkke() throws Exception {
        server.expect(requestTo("/identer?identgruppe=AktoerId"))
                .andRespond(withSuccess(FUNCTIONAL_ERROR_RESPONSE, MediaType.APPLICATION_JSON));

        aktoerConsumer.hentAktoerId("12345678910");
    }
}
