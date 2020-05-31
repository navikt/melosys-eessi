package no.nav.melosys.eessi.models.exception;

public class SecurityException extends RuntimeException {
    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
