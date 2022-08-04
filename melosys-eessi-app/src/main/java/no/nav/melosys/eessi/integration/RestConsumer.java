package no.nav.melosys.eessi.integration;

import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static no.nav.melosys.eessi.config.MDCLogging.X_CORRELATION_ID;
import static no.nav.melosys.eessi.config.MDCLogging.getCorrelationId;

public interface RestConsumer {

    default HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(X_CORRELATION_ID, getCorrelationId());
        return headers;
    }
}
