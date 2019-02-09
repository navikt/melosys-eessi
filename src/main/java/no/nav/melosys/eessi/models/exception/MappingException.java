package no.nav.melosys.eessi.models.exception;

public class MappingException extends Exception {

  public MappingException(String message) {
    super(message);
  }

  public MappingException(String message, Throwable cause) {
    super(message, cause);
  }
}
