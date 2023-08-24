package no.nav.melosys.eessi.security;

import lombok.RequiredArgsConstructor;
import no.nav.melosys.eessi.service.sts.RestStsClient;
import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class GenericContextExchangeFilter implements ExchangeFilterFunction {

    protected final OAuth2AccessTokenService oAuth2AccessTokenService;

    protected final ClientProperties clientProperties;

    private final boolean isSystemToken;

    protected GenericContextExchangeFilter(ClientConfigurationProperties clientConfigurationProperties,
                                           OAuth2AccessTokenService oAuth2AccessTokenService,
                                           String clientName,
                                           boolean isSystemToken) {
        if (!isSystemToken) {
            throw new RuntimeException("Ikke implementert");
        }
        this.oAuth2AccessTokenService = oAuth2AccessTokenService;
        this.isSystemToken = isSystemToken;
        this.clientProperties = Optional.ofNullable(clientConfigurationProperties.getRegistration().get(clientName))
            .orElseThrow(() -> new RuntimeException("Fant ikke OAuth2-config for " + clientName));
    }


    @Nonnull
    @Override
    public Mono<ClientResponse> filter(@Nonnull final ClientRequest clientRequest,
                                       @Nonnull final ExchangeFunction exchangeFunction) {
        return exchangeFunction.exchange(
            withClientRequestBuilder(ClientRequest.from(clientRequest)).build()
        );
    }

    protected ClientRequest.Builder withClientRequestBuilder(ClientRequest.Builder clientRequestBuilder) {
        return clientRequestBuilder.header(HttpHeaders.AUTHORIZATION, getCorrectToken());
    }

    protected String getCorrectToken() {
        if (isSystemToken) {
            return getSystemToken();
        }

        throw new RuntimeException("Ikke implementert");
    }

    protected abstract String getSystemToken();

    private String getUserToken() {
        return "Bearer " + oAuth2AccessTokenService.getAccessToken(clientProperties).getAccessToken();
    }
}
