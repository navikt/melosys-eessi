package no.nav.melosys.eessi.integration.saf;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import no.nav.melosys.eessi.models.exception.IntegrationException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class SafConsumerTest {

    private SafConsumer safConsumer;

    private final static String JOURNALPOST_ID = "143432657";
    private final static String DOKUMENT_ID = "67890";

    private static MockWebServer mockServer;
    private static String rootUri;

    @BeforeAll
    static void setupServer() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        rootUri = String.format("http://localhost:%s", mockServer.getPort());
    }

    @BeforeEach
    public void setup() {
        safConsumer = new SafConsumer(WebClient.builder().baseUrl(rootUri).build());
    }

    @Test
    void hentTilleggsopplysning_ingenFeil_returnererTilleggsopplynsning() throws Exception {
        final String rinaSaksnummer = "64789743";
        mockServer.enqueue(
            new MockResponse()
                .setBody(hentOkRespons())
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        Optional<String> optionalRinasaksnummer = safConsumer.hentRinasakForJournalpost(JOURNALPOST_ID);
        assertThat(optionalRinasaksnummer).contains(rinaSaksnummer);
    }

    @Test
    void hentTilleggsopplysning_medFeil_kasterException() throws Exception {
        mockServer.enqueue(
            new MockResponse()
                .setBody(hentErrorRespons())
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        assertThatExceptionOfType(IntegrationException.class)
                .isThrownBy(() -> safConsumer.hentRinasakForJournalpost("1231"))
                .withMessageContaining("feil1\nfeil2");
    }

    @Test
    void hentDokument_dokumentFinnes_returnererPDF() {
        mockServer.enqueue(
            new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .setBody("pdf")
        );

        byte[] pdf = safConsumer.hentDokument(JOURNALPOST_ID, DOKUMENT_ID);

        assertThat(pdf).isEqualTo(new byte[]{'p', 'd', 'f'});
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
