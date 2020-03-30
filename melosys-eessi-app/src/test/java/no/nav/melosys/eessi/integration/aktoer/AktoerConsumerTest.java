package no.nav.melosys.eessi.integration.aktoer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(MockitoJUnitRunner.class)
public class AktoerConsumerTest {

    private String okResponseAktoer;
    private String okResponseNorskIdent;
    private String functionalErrorResponse;

    @Spy
    private RestTemplate restTemplate;
    @InjectMocks
    private AktoerConsumer aktoerConsumer;

    private MockRestServiceServer server;


    @Before
    public void setUp() throws Exception {
        functionalErrorResponse = hentFil("mock/aktoer_err.json");
        okResponseAktoer = hentFil("mock/aktoer_ok_aktoerres.json");
        okResponseNorskIdent = hentFil("mock/aktoer_ok_identres.json");
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void hentAktoerIdOk() throws Exception {
        server.expect(requestTo("/identer?identgruppe=AktoerId")).andRespond(withSuccess(okResponseAktoer, MediaType.APPLICATION_JSON));
        assertThat(aktoerConsumer.hentAktoerId("06038029973")).isEqualTo("1000004898116");
    }

    @Test
    public void hentNorskIdent() throws Exception {
        server.expect(requestTo("/identer?identgruppe=NorskIdent")).andRespond(withSuccess(okResponseNorskIdent, MediaType.APPLICATION_JSON));
        assertThat(aktoerConsumer.hentNorskIdent("06038029973")).isEqualTo("06069900000");
    }

    @Test(expected = NotFoundException.class)
    public void getAktoerIdIdentFinnesIkke() throws Exception {
        server.expect(requestTo("/identer?identgruppe=AktoerId"))
                .andRespond(withSuccess(functionalErrorResponse, MediaType.APPLICATION_JSON));

        aktoerConsumer.hentAktoerId("12345678910");
    }

    private String hentFil(String filnavn) throws Exception {
        Path path = Paths.get(this.getClass().getClassLoader().getResource(filnavn).toURI());
        return Files.readString(path);
    }
}
