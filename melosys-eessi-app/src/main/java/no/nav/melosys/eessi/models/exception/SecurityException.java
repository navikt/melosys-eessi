package no.nav.melosys.eessi.models.exception;

public class SecurityException extends Exception {

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
