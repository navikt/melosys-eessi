package no.nav.melosys.eessi.integration.pdl;

import jakarta.annotation.Nonnull;

import lombok.extern.slf4j.Slf4j;
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

import static no.nav.melosys.eessi.config.MDCOperations.X_CORRELATION_ID;
import static no.nav.melosys.eessi.config.MDCOperations.getCorrelationId;

@Slf4j
@Component
public class PDLSystemAuthFilter implements ExchangeFilterFunction {

    private final OAuth2AccessTokenService oAuth2AccessTokenService;
    private final ClientProperties clientProperties;
    private final static String CLIENT_NAME = "pdl";
    private static final String NAV_CONSUMER_TOKEN = "Nav-Consumer-Token";

    public PDLSystemAuthFilter(ClientConfigurationProperties clientConfigurationProperties, OAuth2AccessTokenService oAuth2AccessTokenService) {
        this.oAuth2AccessTokenService = oAuth2AccessTokenService;
        this.clientProperties = Optional.ofNullable(clientConfigurationProperties.getRegistration().get(CLIENT_NAME))
            .orElseThrow(() -> new RuntimeException("Fant ikke OAuth2-config for " + CLIENT_NAME));
    }

    @Nonnull
    @Override
    public Mono<ClientResponse> filter(@Nonnull ClientRequest clientRequest,
                                       @Nonnull ExchangeFunction exchangeFunction) {
        final String bearerToken = "Bearer " + oAuth2AccessTokenService.getAccessToken(clientProperties).getAccessToken();
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
