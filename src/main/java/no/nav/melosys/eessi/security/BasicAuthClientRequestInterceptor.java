package no.nav.melosys.eessi.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import no.nav.melosys.eessi.config.EnvironmentHandler;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class BasicAuthClientRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, basicAuth());
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }

    private String basicAuth() {

        String SYSTEM_USERNAME = "melosys.systemuser.username";
        String SYSTEM_PASSWORD = "melosys.systemuser.password";

        return "Basic " + Base64.getEncoder().encodeToString(
                String.format("%s:%s", getEnv().getRequiredProperty(SYSTEM_USERNAME),
                        getEnv().getRequiredProperty(SYSTEM_PASSWORD))
                        .getBytes(StandardCharsets.UTF_8));
    }

    private Environment getEnv() {
        return EnvironmentHandler.getInstance().getEnv();
    }
}
