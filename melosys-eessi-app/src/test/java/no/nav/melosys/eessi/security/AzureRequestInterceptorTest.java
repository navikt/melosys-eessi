package no.nav.melosys.eessi.security;

import java.net.URI;
import java.util.List;
import java.util.Map;

import no.nav.security.token.support.client.core.ClientProperties;
import no.nav.security.token.support.client.core.OAuth2GrantType;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AzureRequestInterceptorTest {

    @Mock
    private OAuth2AccessTokenService oAuth2AccessTokenService;

    private ClientRequestInterceptor clientRequestInterceptor;

    @Mock
    private ClientHttpRequestExecution httpRequestExecution;

    private final String oidcKey = "t43i56oh4yoi5";

    @BeforeEach
    public void setup() {
        var clientConfigurationProperties = new ClientConfigurationProperties(Map.of("eux-rina-api", ClientProperties.builder()
            .wellKnownUrl(URI.create("test"))
            .tokenEndpointUrl(URI.create("token_endpoint"))
            .grantType(OAuth2GrantType.JWT_BEARER)
            .scope(List.of("scope1", "scope2"))
            .resourceUrl(URI.create("resource_url"))
            .build()));

        clientRequestInterceptor = new ClientRequestInterceptor(clientConfigurationProperties, oAuth2AccessTokenService, "eux-rina-api");
        when(oAuth2AccessTokenService.getAccessToken(any())).thenReturn(OAuth2AccessTokenResponse.builder().accessToken(oidcKey).build());
    }

    @Test
    void intercept() throws Exception {
        MockClientHttpRequest httpRequest = new MockClientHttpRequest();
        clientRequestInterceptor.intercept(httpRequest, new byte[0], httpRequestExecution);

        verify(httpRequestExecution).execute(any(HttpRequest.class), any(byte[].class));

        assertThat(httpRequest.getHeaders()).containsKey(HttpHeaders.AUTHORIZATION);
        assertThat(httpRequest.getHeaders().get(HttpHeaders.AUTHORIZATION)).contains("Bearer " + oidcKey);
    }
}
