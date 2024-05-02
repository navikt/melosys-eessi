package no.nav.melosys.eessi.security;

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

import java.util.Optional;

@Component
public class OppgaveSakRequestExchangeFilter implements ExchangeFilterFunction {

    private final OAuth2AccessTokenService oAuth2AccessTokenService;
    private final ClientProperties clientProperties;

    private final String CLIENT_NAME = "oppgave-sak";

    public OppgaveSakRequestExchangeFilter(OAuth2AccessTokenService oAuth2AccessTokenService, ClientConfigurationProperties clientConfigurationProperties) {
        this.oAuth2AccessTokenService = oAuth2AccessTokenService;
        this.clientProperties = Optional.ofNullable(clientConfigurationProperties.getRegistration().get(CLIENT_NAME))
            .orElseThrow(() -> new RuntimeException("Fant ikke OAuth2-config for " + CLIENT_NAME));
    }

    @Nonnull
    @Override
    public Mono<ClientResponse> filter(@Nonnull ClientRequest clientRequest,
                                       @Nonnull ExchangeFunction exchangeFunction) {
        return exchangeFunction.exchange(
            ClientRequest.from(clientRequest)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + oAuth2AccessTokenService.getAccessToken(clientProperties).getAccessToken())
                .build()
        );
    }
}
