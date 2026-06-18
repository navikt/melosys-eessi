package no.nav.melosys.eessi.integration.sak;

import no.nav.melosys.eessi.integration.RestConsumer;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static no.nav.melosys.eessi.config.MDCOperations.X_CORRELATION_ID;
import static no.nav.melosys.eessi.config.MDCOperations.getCorrelationId;

public class SakConsumer implements RestConsumer {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SakConsumer.class);
    private final WebClient webClient;

    public SakConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public Sak getSak(String arkivsakId) {
        var correlationID = getCorrelationId();
        log.info("hentsak: correlationId: {}, sakId: {}", correlationID, arkivsakId);
        return webClient.get()
            .uri("/{arkivsakId}", arkivsakId)
            .header(X_CORRELATION_ID, correlationID)
            .retrieve()
            .onStatus(HttpStatusCode::isError, this::håndterFeil)
            .bodyToMono(Sak.class)
            .block();
    }

    private Mono<? extends Throwable> håndterFeil(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
            .defaultIfEmpty("Ukjent feil")
            .map(body -> new IntegrationException("Feil i integrasjon mot sak: " + body));
    }
}
