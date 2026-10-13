package com.knu.ticketing.application.venue.errors;

import java.util.UUID;

public class VenueNotFoundException extends RuntimeException {

    public VenueNotFoundException(UUID id) {
        super(String.format("Venue with id '%s' is not found", id));
    }
}
