package no.nav.melosys.eessi.models.exception;

public class NotAllowedException extends RuntimeException{
    public NotAllowedException(String message) {
        super(message);
    }
}
