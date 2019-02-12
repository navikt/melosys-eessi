package no.nav.melosys.eessi.security;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.service.sts.RestStsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OidcTokenClientrequestInterceptor implements ClientHttpRequestInterceptor {

    private final RestStsService restStsService;

    public OidcTokenClientrequestInterceptor(RestStsService restStsService) {
        this.restStsService = restStsService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        String token;
        try {
            token = restStsService.collectToken();
        } catch (IntegrationException e) {
            throw new RuntimeException("Could not collect token from sts", e);
        }

        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return execution.execute(request, body);
    }
}
