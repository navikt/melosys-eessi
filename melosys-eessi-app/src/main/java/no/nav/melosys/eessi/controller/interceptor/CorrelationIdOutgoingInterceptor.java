package no.nav.melosys.eessi.controller.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static no.nav.melosys.eessi.config.MDCLogging.X_CORRELATION_ID;
import static no.nav.melosys.eessi.config.MDCLogging.getCorrelationId;

@Component
public class CorrelationIdOutgoingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add(X_CORRELATION_ID, getCorrelationId());
        return execution.execute(request, body);
    }
}
