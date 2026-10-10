package com.knu.ticketing.application.venue.errors;

import com.knu.ticketing.domain.venue.VenueName;

public class VenueAlreadyExistsException extends RuntimeException {

    public VenueAlreadyExistsException(VenueName name) {
        super(String.format("The venue with name '%s' already exists", name.value()));
    }
}
