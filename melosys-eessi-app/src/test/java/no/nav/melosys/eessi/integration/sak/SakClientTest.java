package no.nav.melosys.eessi.integration.sak;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static no.nav.melosys.eessi.config.MDCOperations.X_CORRELATION_ID;
import static org.assertj.core.api.Assertions.assertThat;

class SakClientTest {

    private SakClient sakClient;
    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setupAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void setUp() {
        String rootUri = String.format("http://localhost:%s", mockWebServer.getPort());
        sakClient = new SakClient(WebClient.builder().baseUrl(rootUri).build());
    }

    @Test
    void getSak_expectSak() throws InterruptedException {
        String responseJson = "{\"id\":\"11\",\"tema\":\"MED\",\"applikasjon\":\"melsoys\","
                + "\"aktoerId\":\"123\",\"orgnr\":\"12312312\",\"fagsakNr\":\"fag123\","
                + "\"opprettetAv\":\"srvmelosys\",\"opprettetTidspunkt\":\"2019-02-11T08:33:38.964Z\"}";
        mockWebServer.enqueue(new MockResponse()
            .setBody(responseJson)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Sak response = sakClient.getSak("11");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("11");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).isEqualTo("/11");
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getHeaders().names()).contains(X_CORRELATION_ID);
    }
}
