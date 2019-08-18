package no.nav.melosys.eessi.security;

import no.nav.melosys.eessi.service.sts.RestStsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OidcTokenClientRequestInterceptorTest {

    @Mock
    private RestStsService restStsService;
    @InjectMocks
    private OidcTokenClientRequestInterceptor oidcTokenClientRequestInterceptor;

    @Mock
    private ClientHttpRequestExecution httpRequestExecution;

    private final String oidcKey = "t43i56oh4yoi5";

    @Before
    public void setup() throws Exception {
        when(restStsService.collectToken()).thenReturn(oidcKey);
    }
    @Test
    public void intercept() throws Exception {
        MockClientHttpRequest httpRequest = new MockClientHttpRequest();
        oidcTokenClientRequestInterceptor.intercept(httpRequest, new byte[0], httpRequestExecution);

        verify(httpRequestExecution).execute(any(HttpRequest.class), any(byte[].class));
        verify(restStsService).collectToken();

        assertThat(httpRequest.getHeaders()).containsKey(HttpHeaders.AUTHORIZATION);
        assertThat(httpRequest.getHeaders().get(HttpHeaders.AUTHORIZATION)).contains("Bearer " + oidcKey);
    }
}