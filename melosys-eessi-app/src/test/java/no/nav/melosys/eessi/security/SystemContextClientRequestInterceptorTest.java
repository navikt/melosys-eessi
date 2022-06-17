package no.nav.melosys.eessi.security;

import no.nav.melosys.eessi.service.sts.RestStsClient;
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
class SystemContextClientRequestInterceptorTest {

    @Mock
    private RestStsClient restStsClient;

    private SystemContextClientRequestInterceptor systemContextClientRequestInterceptor;

    @Mock
    private ClientHttpRequestExecution httpRequestExecution;

    private final String oidcKey = "t43i56oh4yoi5";

    @BeforeEach
    public void setup() {
        systemContextClientRequestInterceptor = new SystemContextClientRequestInterceptor(restStsClient);
        when(restStsClient.collectToken()).thenReturn(oidcKey);
    }

    @Test
    void intercept() throws Exception {
        MockClientHttpRequest httpRequest = new MockClientHttpRequest();
        systemContextClientRequestInterceptor.intercept(httpRequest, new byte[0], httpRequestExecution);

        verify(httpRequestExecution).execute(any(HttpRequest.class), any(byte[].class));
        verify(restStsClient).collectToken();

        assertThat(httpRequest.getHeaders()).containsKey(HttpHeaders.AUTHORIZATION);
        assertThat(httpRequest.getHeaders().get(HttpHeaders.AUTHORIZATION)).contains("Bearer " + oidcKey);
    }
}
