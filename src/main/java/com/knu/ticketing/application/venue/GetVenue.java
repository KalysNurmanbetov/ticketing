package com.knu.ticketing.application.venue;

import com.knu.ticketing.application.venue.errors.VenueNotFoundException;
import com.knu.ticketing.domain.venue.Venue;
import com.knu.ticketing.domain.venue.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class GetVenue {

    private final VenueRepository venueRepository;

    public GetVenue(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Transactional(readOnly = true)
    public Venue run(UUID id) {
        Objects.requireNonNull(id, "id should be defined");
        return venueRepository.findById(id).orElseThrow(() -> new VenueNotFoundException(id));
    }
}
