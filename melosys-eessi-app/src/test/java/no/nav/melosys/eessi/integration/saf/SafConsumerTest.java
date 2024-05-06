package no.nav.melosys.eessi.integration.saf;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import no.nav.melosys.eessi.integration.pdl.PDLConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class SafConsumerTest {

    private MockRestServiceServer server;
    private final RestTemplate restTemplate = new RestTemplate();

    private SafConsumer safConsumer;

    private final static String JOURNALPOST_ID = "143432657";

    private static MockWebServer mockServer;

    @BeforeAll
    static void setupServer() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @BeforeEach
    public void setup() {
        server = MockRestServiceServer.createServer(restTemplate);

        safConsumer = new SafConsumer(WebClient.builder().baseUrl(String.format("http://localhost:%s", mockServer.getPort())).build());
    }

    @Test
    void hentTilleggsopplysning_ingenFeil_returnererTilleggsopplynsning() throws Exception {
        final String rinaSaksnummer = "64789743";
        server.expect(requestTo("/graphql")).andRespond(withSuccess(hentOkRespons(), MediaType.APPLICATION_JSON));

        Optional<String> optionalRinasaksnummer = safConsumer.hentRinasakForJournalpost(JOURNALPOST_ID);
        assertThat(optionalRinasaksnummer).contains(rinaSaksnummer);
    }

    @Test
    void hentTilleggsopplysning_medFeil_kasterException() throws Exception {
        server.expect(requestTo("/graphql")).andRespond(withSuccess(hentErrorRespons(), MediaType.APPLICATION_JSON));

        assertThatExceptionOfType(IntegrationException.class)
                .isThrownBy(() -> safConsumer.hentRinasakForJournalpost("1231"))
                .withMessageContaining("feil1\nfeil2");
    }

    private String hentErrorRespons() throws Exception {
        return hentFil("mock/saf-err.json");
    }

    private String hentOkRespons() throws Exception {
        return hentFil("mock/saf.json");
    }

    private String hentFil(String filnavn) throws Exception {
        Path path = Paths.get(Objects.requireNonNull(this.getClass().getClassLoader().getResource(filnavn)).toURI());
        return Files.readString(path);
    }
}
