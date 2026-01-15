package no.nav.melosys.eessi.security;


import java.util.Base64;
import java.util.List;
import java.util.Map;

import no.nav.melosys.eessi.config.AppCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BasicAuthClientRequestInterceptorTest {

    private BasicAuthClientRequestInterceptor interceptor;
    private AppCredentials appCredentials;

    @BeforeEach
    public void setup() {
        appCredentials = new AppCredentials();
        appCredentials.setUsername("usr");
        appCredentials.setPassword("pwd");

        interceptor = new BasicAuthClientRequestInterceptor(appCredentials);
    }

    @Test
    void intercept_medGyldigAuth_verifiserToken() throws Exception {
        String expectedToken = "Basic " + Base64.getEncoder().encodeToString(
                (appCredentials.getUsername() + ":" + appCredentials.getPassword()).getBytes()
        );

        ClientHttpRequestExecution httpRequestExecution = mock(ClientHttpRequestExecution.class);

        MockClientHttpRequest httpRequest = new MockClientHttpRequest();
        interceptor.intercept(httpRequest, new byte[0], httpRequestExecution);

        verify(httpRequestExecution).execute(any(HttpRequest.class), any(byte[].class));

        HttpHeaders headers = httpRequest.getHeaders();

        List<String> authValues = headers.get(HttpHeaders.AUTHORIZATION);
        assertThat(authValues).contains(expectedToken);
    }
}
