package com.knu.ticketing.domain.venue;

import java.util.Optional;
import java.util.UUID;

public interface VenueRepository {
    Optional<Venue> findById(UUID id);

    Optional<Venue> findByName(VenueName name);

    Venue save(Venue venue);

}
