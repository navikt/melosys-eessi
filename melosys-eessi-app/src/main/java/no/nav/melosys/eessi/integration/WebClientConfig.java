package no.nav.melosys.eessi.integration;

import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public interface WebClientConfig {
    default ExchangeFilterFunction errorFilter(String feilmelding) {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (response.statusCode().isError()) {
                return response.bodyToMono(String.class)
                    .defaultIfEmpty(response.statusCode().toString())
                    .flatMap(
                        errorBody -> Mono.error(new RuntimeException(feilmelding + " " + response.statusCode() + " - " + errorBody)));
            }
            return Mono.just(response);
        });
    }
}
