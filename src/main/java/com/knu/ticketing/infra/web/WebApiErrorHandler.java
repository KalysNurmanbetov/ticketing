package com.knu.ticketing.infra.web;

import com.knu.ticketing.application.venue.errors.VenueAlreadyExistsException;
import com.knu.ticketing.application.venue.errors.VenueNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class WebApiErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(VenueAlreadyExistsException.class)
    ProblemDetail handle(VenueAlreadyExistsException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(VenueNotFoundException.class)
    ProblemDetail handle(VenueNotFoundException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handle(IllegalArgumentException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail handle(IllegalStateException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.getMessage());
    }
}
