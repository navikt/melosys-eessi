package no.nav.melosys.eessi.security;

import java.io.IOException;
import java.util.Optional;

import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class SystemContextClientRequestInterceptor implements ClientHttpRequestInterceptor {
    private final OAuth2AccessTokenService oAuth2AccessTokenService;
    private final ClientProperties clientProperties;

    public SystemContextClientRequestInterceptor(OAuth2AccessTokenService oAuth2AccessTokenService, ClientConfigurationProperties clientConfigurationProperties, String clientName) {
        this.oAuth2AccessTokenService = oAuth2AccessTokenService;
        this.clientProperties = Optional.ofNullable(clientConfigurationProperties.getRegistration().get(clientName))
            .orElseThrow(() -> new RuntimeException("Fant ikke OAuth2-config for " + clientName));
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String token = oAuth2AccessTokenService.getAccessToken(clientProperties).getAccessToken();
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return execution.execute(request, body);
    }
}
