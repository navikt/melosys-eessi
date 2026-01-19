package no.nav.melosys.eessi.security;

import java.net.URI;
import java.util.Map;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import no.nav.security.token.support.client.core.ClientAuthenticationProperties;
import no.nav.security.token.support.client.core.ClientProperties;
import static com.nimbusds.oauth2.sdk.GrantType.JWT_BEARER;
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
        var authentication = ClientAuthenticationProperties.builder("test-client-id", ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .clientSecret("test-secret")
            .build();
        var clientConfigurationProperties = new ClientConfigurationProperties(Map.of("eux-rina-api",
            ClientProperties.builder(JWT_BEARER, authentication)
                .tokenEndpointUrl(URI.create("http://token-endpoint"))
                .build()));

        clientRequestInterceptor = new ClientRequestInterceptor(clientConfigurationProperties, oAuth2AccessTokenService, "eux-rina-api");
        when(oAuth2AccessTokenService.getAccessToken(any())).thenReturn(new OAuth2AccessTokenResponse(oidcKey, 3600, 3600, Map.of()));
    }

    @Test
    void intercept() throws Exception {
        MockClientHttpRequest httpRequest = new MockClientHttpRequest();
        clientRequestInterceptor.intercept(httpRequest, new byte[0], httpRequestExecution);

        verify(httpRequestExecution).execute(any(HttpRequest.class), any(byte[].class));

        HttpHeaders headers = httpRequest.getHeaders();
        assertThat(headers.containsHeader(HttpHeaders.AUTHORIZATION)).isTrue();
        assertThat(headers.get(HttpHeaders.AUTHORIZATION)).contains("Bearer " + oidcKey);
    }
}
