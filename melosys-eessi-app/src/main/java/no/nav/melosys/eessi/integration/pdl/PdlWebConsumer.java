package no.nav.melosys.eessi.integration.pdl;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.pdl.web.identrekvisisjon.dto.IdentRekvisisjonTilMellomlagring;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class PdlWebConsumer implements RestConsumer {

    private final WebClient webClient;

    public PdlWebConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public String hentLenkeForRekvirering(IdentRekvisisjonTilMellomlagring identRekvisisjonTilMellomlagring) {
        return webClient
            .post()
            .uri("/api/identrekvisisjon/mellomlagring")
            .bodyValue(identRekvisisjonTilMellomlagring)
            .retrieve()
            .toEntity(String.class)
            .mapNotNull(response -> response.getHeaders().getFirst("Location"))
            .block();
    }
}
