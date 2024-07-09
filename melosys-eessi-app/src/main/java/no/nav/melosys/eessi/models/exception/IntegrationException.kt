package no.nav.melosys.eessi.models.exception;

public class IntegrationException extends RuntimeException {

    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(String message, Throwable e) {
        super(message, e);
    }
}
