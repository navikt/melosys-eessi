package no.nav.melosys.eessi.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotAllowedException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity handle(NotFoundException e) {
        return handle(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MappingException.class)
    public ResponseEntity handle(MappingException e) {
        return handle(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IntegrationException.class)
    public ResponseEntity handle(IntegrationException e) {
        return handle(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NotAllowedException.class)
    public ResponseEntity handle(NotAllowedException e) {
        return handle(e, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handle(Exception e) {
        return handle(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity handle(Exception e, HttpStatus httpStatus) {
        log.error("Feil oppstått: ", e);
        Map<String, String> entity = new HashMap<>();
        entity.put("error", httpStatus.getReasonPhrase());
        entity.put("message", e.getMessage());
        return new ResponseEntity<>(entity, httpStatus);
    }
}
