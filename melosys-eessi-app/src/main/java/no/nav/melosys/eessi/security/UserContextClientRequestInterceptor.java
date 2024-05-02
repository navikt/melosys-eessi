package no.nav.melosys.eessi.security;

import java.io.IOException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.OAuth2GrantType;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import static no.nav.security.token.support.client.core.OAuth2GrantType.JWT_BEARER;

@Slf4j
public class UserContextClientRequestInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AccessTokenService oAuth2AccessTokenService;

    private final ClientProperties clientProperties;

    public UserContextClientRequestInterceptor(ClientConfigurationProperties clientConfigurationProperties,
                                               OAuth2AccessTokenService oAuth2AccessTokenService,
                                               String clientName) {
        this.oAuth2AccessTokenService = oAuth2AccessTokenService;
        this.clientProperties = Optional.ofNullable(clientConfigurationProperties.getRegistration().get(clientName))
            .orElseThrow(() -> new RuntimeException("Fant ikke OAuth2-config for " + clientName));
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + hentAccessToken());
        return execution.execute(request, body);
    }

    private String hentAccessToken() {
        try {
            if (ContextHolder.getInstance().canExchangeOBOToken()) {
                OAuth2AccessTokenResponse response = oAuth2AccessTokenService.getAccessToken(clientProperties);
                return response.getAccessToken();
            } else if (clientProperties.getGrantType().equals(JWT_BEARER)) {
                return hentAccessTokenForSystem();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            log.error("Feil under henting av access token", e);
            throw new RuntimeException(e);
        }
    }

    private String hentAccessTokenForSystem() {
        var clientPropertiesForSystem = ClientProperties.builder()
            .tokenEndpointUrl(clientProperties.getTokenEndpointUrl())
            .scope(clientProperties.getScope())
            .authentication(clientProperties.getAuthentication())
            .grantType(OAuth2GrantType.CLIENT_CREDENTIALS)
            .build();
        OAuth2AccessTokenResponse response = oAuth2AccessTokenService.getAccessToken(clientPropertiesForSystem);
        return  response.getAccessToken();
    }
}
