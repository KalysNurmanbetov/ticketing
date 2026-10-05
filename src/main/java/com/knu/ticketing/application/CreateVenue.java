package com.knu.ticketing.application;

import com.knu.ticketing.domain.venue.Venue;
import com.knu.ticketing.domain.venue.VenueName;
import com.knu.ticketing.domain.venue.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateVenue {

    private final VenueRepository venueRepository;

    public CreateVenue(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Transactional
    public UUID run(String name) {
        VenueName venueName = new VenueName(name);
        Venue venue = Venue.create(UUID.randomUUID(), venueName);

        if (venueRepository
                .findByName(venueName)
                .isPresent()) {
            throw new IllegalStateException(String.format(
                    "venue with name '%s' is already created",
                    venueName.value()
            ));
        }

        return venueRepository
                .save(venue)
                .getId();
    }
}
