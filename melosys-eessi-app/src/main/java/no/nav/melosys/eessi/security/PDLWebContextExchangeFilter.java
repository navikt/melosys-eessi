package no.nav.melosys.eessi.security;

import java.util.Optional;
import jakarta.annotation.Nonnull;

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

@Component
public class PDLWebContextExchangeFilter implements ExchangeFilterFunction {

    private final OAuth2AccessTokenService oAuth2AccessTokenService;

    private final ClientProperties clientProperties;
    private final static String CLIENT_NAME = "pdl-web";


    public PDLWebContextExchangeFilter(ClientConfigurationProperties clientConfigurationProperties,
                                       OAuth2AccessTokenService oAuth2AccessTokenService) {
        this.oAuth2AccessTokenService = oAuth2AccessTokenService;
        this.clientProperties = Optional.ofNullable(clientConfigurationProperties.getRegistration().get(CLIENT_NAME))
            .orElseThrow(() -> new RuntimeException("Fant ikke OAuth2-config for " + CLIENT_NAME));
    }


    @Nonnull
    @Override
    public Mono<ClientResponse> filter(@Nonnull final ClientRequest clientRequest,
                                       @Nonnull final ExchangeFunction exchangeFunction) {
        return exchangeFunction.exchange(
            withClientRequestBuilder(ClientRequest.from(clientRequest)).build()
        );
    }

    private ClientRequest.Builder withClientRequestBuilder(ClientRequest.Builder clientRequestBuilder) {
        return clientRequestBuilder.header(HttpHeaders.AUTHORIZATION, getSystemToken());
    }

    private String getSystemToken() {
        return "Bearer " + oAuth2AccessTokenService.getAccessToken(clientProperties).getAccessToken();
    }
}
