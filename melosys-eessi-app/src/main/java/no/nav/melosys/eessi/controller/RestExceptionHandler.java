package no.nav.melosys.eessi.controller;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import no.nav.melosys.eessi.models.exception.IntegrationException;
import no.nav.melosys.eessi.models.exception.MappingException;
import no.nav.melosys.eessi.models.exception.NotFoundException;
import no.nav.melosys.eessi.models.exception.ValidationException;
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

    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity handle(ValidationException e) {
        return handle(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IntegrationException.class)
    public ResponseEntity handle(IntegrationException e) {
        return handle(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handle(Exception e) {
        return handle(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity handle(Exception e, HttpStatus httpStatus) {
        String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        log.error("Feil oppstått: {}", message, e);
        Map<String, String> entity = new HashMap<>();
        entity.put("error", httpStatus.getReasonPhrase());
        entity.put("message", message);
        return new ResponseEntity<>(entity, httpStatus);
    }
}
