package no.nav.melosys.eessi.security;

import java.io.IOException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.service.sts.RestStsClient;
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
public class UserContextClientRequestInterceptor implements ClientHttpRequestInterceptor {

    private final RestStsClient restStsClient;
    private final OAuth2AccessTokenService oAuth2AccessTokenService;

    private final ClientProperties clientProperties;

    public UserContextClientRequestInterceptor(RestStsClient restStsClient,
                                               ClientConfigurationProperties clientConfigurationProperties,
                                               OAuth2AccessTokenService oAuth2AccessTokenService,
                                               String clientName) {
        this.oAuth2AccessTokenService = oAuth2AccessTokenService;
        this.restStsClient = restStsClient;
        this.clientProperties = Optional.ofNullable(clientConfigurationProperties.getRegistration().get(clientName))
            .orElseThrow(() -> new RuntimeException("Fant ikke OAuth2-config for " + clientName));
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + genererAccessToken(request));
        return execution.execute(request, body);
    }

    private String genererAccessToken(HttpRequest request) {
        if (ContextHolder.getInstance().canExchangeOBOToken()) {
            OAuth2AccessTokenResponse response = oAuth2AccessTokenService.getAccessToken(clientProperties);
            return response.getAccessToken();
        } else if (request.getURI().toString().contains("eux-rina-api")) {
            var clientPropertiesForSystem = ClientProperties.builder()
                .tokenEndpointUrl(clientProperties.getTokenEndpointUrl())
                .scope(clientProperties.getScope())
                .authentication(clientProperties.getAuthentication())
                .grantType(OAuth2GrantType.CLIENT_CREDENTIALS)
                .build();
            OAuth2AccessTokenResponse response = oAuth2AccessTokenService.getAccessToken(clientPropertiesForSystem);
            return response.getAccessToken();
        } else {
            return restStsClient.collectToken();
        }
    }
}
