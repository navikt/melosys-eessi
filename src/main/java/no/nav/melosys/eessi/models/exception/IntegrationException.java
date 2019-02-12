package no.nav.melosys.eessi.models.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class IntegrationException extends Exception {

    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(String message, Throwable e) {
        super(message, e);
    }
}
