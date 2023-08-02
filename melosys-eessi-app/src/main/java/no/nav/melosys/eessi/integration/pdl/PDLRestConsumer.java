package no.nav.melosys.eessi.integration.pdl;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.integration.RestUtils;
import no.nav.melosys.eessi.integration.pdl.dto.sed.PDLSed;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static no.nav.melosys.eessi.config.MDCOperations.X_CORRELATION_ID;
import static no.nav.melosys.eessi.config.MDCOperations.getCorrelationId;
@Slf4j
public class PDLRestConsumer implements RestConsumer {

    private final WebClient webClient;

    public PDLRestConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public String hentPreutfylltLenkeForRekvirering(PDLSed pdlSed) {
        log.info("[EESSI TEST] Prøver å kommunisere med PDL WEB");
        return webClient.post()
            .uri("/api/sed")
            .bodyValue(pdlSed)
            .retrieve()
            .onStatus(HttpStatus::isError, this::håndterFeil)
            .bodyToMono(String.class)
            .block();
    }

    private Mono<? extends Throwable> håndterFeil(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
            .map(RestUtils::hentFeilmeldingForOppgave)
            .map(IntegrationException::new);
    }
}
