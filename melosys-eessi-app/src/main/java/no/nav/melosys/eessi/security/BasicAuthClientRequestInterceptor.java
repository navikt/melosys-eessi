package no.nav.melosys.eessi.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import no.nav.melosys.eessi.config.AppCredentials;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class BasicAuthClientRequestInterceptor implements ClientHttpRequestInterceptor {

    private final AppCredentials appCredentials;

    public BasicAuthClientRequestInterceptor(AppCredentials appCredentials) {
        this.appCredentials = appCredentials;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, basicAuth());
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }

    private String basicAuth() {
        return "Basic " + Base64.getEncoder().encodeToString(
            String.format("%s:%s",
                appCredentials.getUsername(),
                appCredentials.getPassword()
            ).getBytes(StandardCharsets.UTF_8));
    }
}
