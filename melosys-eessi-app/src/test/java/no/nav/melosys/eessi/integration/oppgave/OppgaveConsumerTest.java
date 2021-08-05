package no.nav.melosys.eessi.integration.oppgave;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class OppgaveConsumerTest {

    private OppgaveConsumer oppgaveConsumer;

    private final String OPPGAVE_ID = "123";

    private static MockWebServer mockWebServer;
    private static String rootUri;

    @BeforeAll
    static void setupAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        rootUri = String.format("http://localhost:%s", mockWebServer.getPort());
    }

    @BeforeEach
    public void setUp() {
        oppgaveConsumer = new OppgaveConsumer(WebClient.builder().baseUrl(rootUri).build());
    }

    @Test
    void hentOppgave_oppgaveFinnes_verifiserMapping() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(hentOppgaveResponse())
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        var oppgave = oppgaveConsumer.hentOppgave(OPPGAVE_ID);
        assertOppgaveFelter(oppgave);
        var request = mockWebServer.takeRequest();
        assertThat(request)
                .extracting(RecordedRequest::getPath, RecordedRequest::getMethod)
                .containsExactly("/oppgaver/" + OPPGAVE_ID, "GET");
        assertThat(request.getHeaders().names()).contains(OppgaveConsumer.X_CORRELATION_ID);
    }

    @Test
    void opprettOppgave_verifiserMapping() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(hentOppgaveResponse())
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        var oppgave = oppgaveConsumer.opprettOppgave(opprettOppgave());
        assertOppgaveFelter(oppgave);
        var request = mockWebServer.takeRequest();
        assertThat(request)
                .extracting(RecordedRequest::getPath, RecordedRequest::getMethod)
                .containsExactly("/oppgaver", "POST");
        assertThat(request.getHeaders().names()).contains(OppgaveConsumer.X_CORRELATION_ID);
    }

    @Test
    void oppdaterOppgave_utenBeskrivelse_beksrivelseMappesIkkeTilRequest() throws InterruptedException, JsonProcessingException {
        final var forventetJsonBodyRequestUtenBeskrivelseFelt = """
                {
                    "id": 1,
                    "versjon": 2,
                    "status": "status"
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(hentOppgaveResponse())
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        var oppgaveOppdateringDto = OppgaveOppdateringDto.builder().id(1).versjon(2).status("status").build();
        assertOppgaveFelter(oppgaveConsumer.oppdaterOppgave(OPPGAVE_ID, oppgaveOppdateringDto));

        var request = mockWebServer.takeRequest();
        var requestBody = request.getBody().readUtf8();
        assertThat(request)
                .extracting(RecordedRequest::getPath, RecordedRequest::getMethod)
                .containsExactly("/oppgaver/" + OPPGAVE_ID, "PATCH");
        assertThat(request.getHeaders().names()).contains(OppgaveConsumer.X_CORRELATION_ID);

        var objectMapper = new ObjectMapper();
        assertThat(objectMapper.readTree(requestBody))
                .isEqualTo(objectMapper.readTree(forventetJsonBodyRequestUtenBeskrivelseFelt));
    }

    private void assertOppgaveFelter(HentOppgaveDto oppgaveDto) {
        assertThat(oppgaveDto)
                .extracting(HentOppgaveDto::getStatuskategori, HentOppgaveDto::getAktoerId, HentOppgaveDto::getTema)
                .containsExactly("AAPEN", "1332607802528", "MED");
    }

    private OppgaveDto opprettOppgave() {
        return OppgaveDto.builder()
                .aktivDato(LocalDate.now())
                .fristFerdigstillelse(LocalDate.now().plusDays(1))
                .journalpostId("11111")
                .aktoerId("1332607802528")
                .tema("MED")
                .tildeltEnhetsnr("4530")
                .build();
    }

    @SneakyThrows
    private String hentOppgaveResponse() {
        return new String(
                Files.readAllBytes(
                        Paths.get(
                                Optional.ofNullable(getClass().getClassLoader().getResource("mock/oppgave_get.json"))
                                        .orElseThrow()
                                        .toURI()
                        )
                )
        );
    }
}
