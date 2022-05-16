package no.nav.melosys.eessi.integration.pdl;

import javax.annotation.Nonnull;

import no.nav.melosys.eessi.service.sts.RestSts;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
public class PDLSystemAuthFilter implements ExchangeFilterFunction {

    private final RestSts restSts;
    private static final String NAV_CONSUMER_TOKEN = "Nav-Consumer-Token";

    public PDLSystemAuthFilter(RestSts restSts) {
        this.restSts = restSts;
    }

    @Nonnull
    @Override
    public Mono<ClientResponse> filter(@Nonnull ClientRequest clientRequest,
                                       @Nonnull ExchangeFunction exchangeFunction) {
        final String bearerToken = restSts.bearerToken();
        return exchangeFunction.exchange(
            ClientRequest.from(clientRequest)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .header(NAV_CONSUMER_TOKEN, bearerToken)
                .build()
        );
    }
}
