package no.nav.melosys.eessi.integration.interceptor;

import no.nav.melosys.eessi.config.MDCOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdExchangeFilter implements ExchangeFilterFunction {

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        ClientRequest newRequest = ClientRequest.from(request)
            .header(MDCOperations.X_CORRELATION_ID, MDCOperations.getCorrelationId())
            .build();
        return next.exchange(newRequest);
    }
} 