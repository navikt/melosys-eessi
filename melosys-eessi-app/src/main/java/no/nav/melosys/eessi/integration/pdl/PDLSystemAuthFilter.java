package no.nav.melosys.eessi.integration.pdl;

import javax.annotation.Nonnull;

import no.nav.melosys.eessi.service.sts.RestStsClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import static no.nav.melosys.eessi.config.MDCOperations.X_CORRELATION_ID;
import static no.nav.melosys.eessi.config.MDCOperations.getCorrelationId;

@Component
public class PDLSystemAuthFilter implements ExchangeFilterFunction {

    private final RestStsClient restStsClient;
    private static final String NAV_CONSUMER_TOKEN = "Nav-Consumer-Token";

    public PDLSystemAuthFilter(RestStsClient restStsClient) {
        this.restStsClient = restStsClient;
    }

    @Nonnull
    @Override
    public Mono<ClientResponse> filter(@Nonnull ClientRequest clientRequest,
                                       @Nonnull ExchangeFunction exchangeFunction) {
        final String bearerToken = restStsClient.bearerToken();
        var correlationID = getCorrelationId();
        return exchangeFunction.exchange(
            ClientRequest.from(clientRequest)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .header(X_CORRELATION_ID, correlationID)
                .header(NAV_CONSUMER_TOKEN, bearerToken)
                .build()
        );
    }
}
