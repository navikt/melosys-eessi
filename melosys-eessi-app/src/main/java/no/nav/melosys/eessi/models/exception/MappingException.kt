package no.nav.melosys.eessi.models.exception;

public class MappingException extends RuntimeException {

    public MappingException(String message) {
        super(message);
    }

    public MappingException() {
        super();
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
