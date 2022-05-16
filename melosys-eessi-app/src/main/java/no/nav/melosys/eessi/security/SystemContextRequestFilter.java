package no.nav.melosys.eessi.security;

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
public class SystemContextRequestFilter implements ExchangeFilterFunction {

    private final RestSts restSts;

    public SystemContextRequestFilter(RestSts restSts) {
        this.restSts = restSts;
    }

    @Nonnull
    @Override
    public Mono<ClientResponse> filter(@Nonnull ClientRequest clientRequest,
                                       @Nonnull ExchangeFunction exchangeFunction) {
        return exchangeFunction.exchange(
            ClientRequest.from(clientRequest)
                .header(HttpHeaders.AUTHORIZATION, restSts.bearerToken())
                .build()
        );
    }
}
