package no.nav.melosys.eessi.security;

import java.io.IOException;

import no.nav.melosys.eessi.service.sts.RestStsClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class RestSTSInterceptor implements ClientHttpRequestInterceptor {

    private final RestStsClient restStsClient;

    public RestSTSInterceptor(RestStsClient restStsClient) {
        this.restStsClient = restStsClient;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String token = restStsClient.collectToken();
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return execution.execute(request, body);
    }
}
