package no.nav.melosys.eessi.integration.pdl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import lombok.SneakyThrows;
import no.nav.melosys.eessi.integration.pdl.dto.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class PDLConsumerTest {

    private static MockWebServer mockServer;

    private PDLConsumer pdlConsumer;

    @BeforeAll
    static void setupServer() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @BeforeEach
    public void setup() {
        pdlConsumer = new PDLConsumer(WebClient.builder().baseUrl(String.format("http://localhost:%s", mockServer.getPort())).build());
    }

    @Test
    void hentPerson_medIdent_mottarPersonResponseUtenFeil() {
        mockServer.enqueue(
                new MockResponse()
                        .setBody(hentFil("mock/pdl_hent_person.json"))
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );
        var res = pdlConsumer.hentPerson("123123123");
        assertThat(res.getNavn())
                .flatExtracting(PDLNavn::getFornavn, PDLNavn::getEtternavn)
                .containsExactly("RASK", "MASKIN");
        assertThat(res.getFoedsel())
                .flatExtracting(PDLFoedsel::getFoedselsdato)
                .containsExactly(LocalDate.of(1984, 6, 25));
        assertThat(res.getStatsborgerskap())
                .flatExtracting(PDLStatsborgerskap::getLand)
                .containsExactly("NOR");
        assertThat(res.getFolkeregisterpersonstatus())
                .flatExtracting(PDLFolkeregisterPersonstatus::getStatus)
                .containsExactly("bosatt");
    }

    @Test
    void søkPerson_medRequest_mottarOgMapperResponseUtenFeil() {
        mockServer.enqueue(
                new MockResponse()
                .setBody(hentFil("mock/pdl_sok_person.json"))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        var request = new PDLSokRequestVars(
                new PDLPaging(1, 1),
                Set.of(PDLSokCriteria.etternavn().erLik("Mannen"))
        );

        var response = pdlConsumer.søkPerson(request);
        assertThat(response.getTotalHits()).isOne();
        assertThat(response.getHits())
                .hasSize(1)
                .flatExtracting(PDLSokHits::getIdenter)
                .hasSize(2)
                .containsExactly(
                        new PDLIdent("FOLKEREGISTERIDENT", "28026522600"),
                        new PDLIdent("AKTORID", "2834873315250")
                );
    }

    @Test
    void hentIdentliste_medIdent_mottarOgMapperResponseUtenFeil() {
        mockServer.enqueue(
                new MockResponse()
                .setBody(hentFil("mock/pdl_hent_identliste.json"))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        assertThat(pdlConsumer.hentIdenter("123").getIdenter())
                .hasSize(2)
                .flatExtracting(PDLIdent::getIdent, PDLIdent::getGruppe)
                .containsExactlyInAnyOrder("28026522600", "FOLKEREGISTERIDENT", "2834873315250", "AKTORID");
    }

    @SneakyThrows
    private String hentFil(String filnavn) {
        return Files.readString(
                Paths.get(Objects.requireNonNull(PDLConsumer.class.getClassLoader().getResource(filnavn)).toURI())
        );
    }
}
