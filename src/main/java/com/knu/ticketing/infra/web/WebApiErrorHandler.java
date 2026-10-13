package com.knu.ticketing.infra.web;

import com.knu.ticketing.application.venue.errors.VenueAlreadyExistsException;
import com.knu.ticketing.application.venue.errors.VenueNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

import static com.knu.ticketing.infra.repositories.venue.VenueRepositoryAdapter.VENUE_NAME_KEY_INDEX;

@RestControllerAdvice
public class WebApiErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebApiErrorHandler.class);

    @ExceptionHandler(VenueAlreadyExistsException.class)
    ProblemDetail handle(VenueAlreadyExistsException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(VenueNotFoundException.class)
    ProblemDetail handle(VenueNotFoundException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, error.getMessage());
    }

    //TODO: Change to more accurate exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handle(IllegalArgumentException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.getMessage());
    }

    //TODO: Change to more accurate exceptions
    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail handle(IllegalStateException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handle(DataIntegrityViolationException error) {
        if (error.getCause() instanceof ConstraintViolationException && Objects.equals(
                ((ConstraintViolationException) error.getCause())
                        .getConstraintName(), VENUE_NAME_KEY_INDEX
        )) {
            return ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    "The venue with given name exists already"
            );
        }
        log.error("unexpected data integrity violation", error);
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error: Please ask your administrator about the error"
        );
    }
}
