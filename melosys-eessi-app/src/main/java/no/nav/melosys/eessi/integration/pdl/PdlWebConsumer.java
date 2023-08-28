package no.nav.melosys.eessi.integration.pdl;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.pdl.dto.sed.DnummerRekvisjonTilMellomlagring;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class PdlWebConsumer implements RestConsumer {

    private final WebClient webClient;

    public PdlWebConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public String hentPreutfylltLenkeForRekvirering(DnummerRekvisjonTilMellomlagring dnummerRekvisjonTilMellomlagring) {
        return webClient
            .post()
            .uri("/api/sed")
            .bodyValue(dnummerRekvisjonTilMellomlagring)
            .retrieve()
            .toEntity(String.class)
            .mapNotNull(response -> response.getHeaders().getFirst("Location"))
            .block();
    }
}
