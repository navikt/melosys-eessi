package no.nav.melosys.eessi.models.exception;

public class IntegrationException extends Exception {

    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(String message, Throwable e) {
        super(message, e);
    }
}
