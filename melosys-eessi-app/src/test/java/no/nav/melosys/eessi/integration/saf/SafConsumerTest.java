package no.nav.melosys.eessi.integration.saf;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class SafConsumerTest {

    private MockRestServiceServer server;
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    private SafConsumer safConsumer;

    private final String journalpostID = "143432657";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        server = MockRestServiceServer.createServer(restTemplate);
        safConsumer = new SafConsumer(restTemplate);
    }

    @Test
    public void hentTilleggsopplysning_ingenFeil_returnererTilleggsopplynsning() throws Exception {
        final String rinaSaksnummer = "64789743";
        server.expect(requestTo("/graphql")).andRespond(withSuccess(hentOkRespons(), MediaType.APPLICATION_JSON));

        Optional<String> optionalRinasaksnummer = safConsumer.hentRinasakForJournalpost(journalpostID);
        assertThat(optionalRinasaksnummer).contains(rinaSaksnummer);
    }

    @Test
    public void hentTilleggsopplysning_medFeil_kasterException() throws Exception {
        server.expect(requestTo("/graphql")).andRespond(withSuccess(hentErrorRespons(), MediaType.APPLICATION_JSON));

        expectedException.expect(IntegrationException.class);
        expectedException.expectMessage("feil1\nfeil2");

        safConsumer.hentRinasakForJournalpost("1231");
    }

    private String hentErrorRespons() throws Exception {
        return hentFil("mock/saf-err.json");
    }

    private String hentOkRespons() throws Exception {
        return hentFil("mock/saf.json");
    }

    private String hentFil(String filnavn) throws Exception {
        Path path = Paths.get(this.getClass().getClassLoader().getResource(filnavn).toURI());
        return Files.readString(path);
    }
}
