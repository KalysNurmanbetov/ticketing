package com.knu.ticketing.domain.venue;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VenueTest {


    @Test
    void create_success() {
        UUID id = UUID.randomUUID();
        VenueName name = new VenueName("Red room");
        Venue venue = Venue.create(id, name);

        assertThat(venue.getId()).isEqualTo(id);
        assertThat(venue.getName()).isEqualTo(name);
    }
}
