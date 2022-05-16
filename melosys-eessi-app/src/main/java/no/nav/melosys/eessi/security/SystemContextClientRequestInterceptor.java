package no.nav.melosys.eessi.security;

import java.io.IOException;

import no.nav.melosys.eessi.service.sts.RestSts;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class SystemContextClientRequestInterceptor implements ClientHttpRequestInterceptor {

    private final RestSts restSts;

    public SystemContextClientRequestInterceptor(RestSts restSts) {
        this.restSts = restSts;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String token = restSts.collectToken();
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return execution.execute(request, body);
    }
}
