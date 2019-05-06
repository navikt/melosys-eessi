package no.nav.melosys.eessi.integration;

import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public interface RestConsumer {

    default HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
