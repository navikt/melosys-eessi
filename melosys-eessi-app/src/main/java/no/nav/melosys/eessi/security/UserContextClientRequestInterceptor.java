package no.nav.melosys.eessi.security;

import java.io.IOException;

import no.nav.melosys.eessi.service.sts.RestStsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class UserContextClientRequestInterceptor implements ClientHttpRequestInterceptor {

    private final RestStsService restStsService;

    public UserContextClientRequestInterceptor(RestStsService restStsService) {
        this.restStsService = restStsService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String token = ContextHolder.getInstance().getOidcToken().orElseGet(restStsService::collectToken);
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return execution.execute(request, body);
    }
}
