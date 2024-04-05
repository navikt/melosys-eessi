package no.nav.melosys.eessi.security;

import java.io.IOException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.service.sts.RestStsClient;
import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
@Slf4j
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
        String accessToken = "";
        if (ContextHolder.getInstance().canExchangeOBOToken()) { //TODO && request.getURI() != "eux")
            log.info("Using azure");
            log.info("Debugging i Q2 grant type: " + clientProperties.getGrantType());
            OAuth2AccessTokenResponse response = oAuth2AccessTokenService.getAccessToken(clientProperties);
            log.info("Debugging i Q2: " + response);
            accessToken = response.getAccessToken();
        } else {
            log.info("Using sts");
            accessToken = restStsClient.collectToken();
        }

        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        log.info("Debugging i Q2: " + request);
        return execution.execute(request, body);
    }
}
