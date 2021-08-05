package no.nav.melosys.eessi.security;

import javax.annotation.Nonnull;

import no.nav.melosys.eessi.service.sts.RestStsService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
public class SystemContextRequestFilter implements ExchangeFilterFunction {

    private final RestStsService restStsService;

    public SystemContextRequestFilter(RestStsService restStsService) {
        this.restStsService = restStsService;
    }

    @Nonnull
    @Override
    public Mono<ClientResponse> filter(@Nonnull ClientRequest clientRequest,
                                       @Nonnull ExchangeFunction exchangeFunction) {
        return exchangeFunction.exchange(
                ClientRequest.from(clientRequest)
                        .header(HttpHeaders.AUTHORIZATION, restStsService.bearerToken())
                        .build()
        );
    }
}
