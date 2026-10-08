package com.knu.ticketing.application.venue;

import com.knu.ticketing.domain.venue.Venue;
import com.knu.ticketing.domain.venue.VenueName;
import com.knu.ticketing.domain.venue.VenueRepository;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateVenueTest {

    private final InMemoryVenueRepository venueRepository = new InMemoryVenueRepository();
    private final CreateVenue createVenue = new CreateVenue(venueRepository);

    @Test
    void create_validName_savesVenue() {
        String name = "Red room";
        UUID uuid = createVenue.run(name);

        assertThat(venueRepository.findById(uuid))
                .get()
                .extracting(Venue::getName)
                .extracting(VenueName::value)
                .isEqualTo(name);
    }

    @Test
    void create_duplicatedName_throwsAndSaveNothing() {
        String name = "Red room";
        venueRepository.save(Venue.create(UUID.randomUUID(), new VenueName(name)));

        assertThatThrownBy(() -> createVenue.run(name))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("name");

        assertThat(venueRepository.size()).isEqualTo(1);
    }

    @Test
    void create_anotherName_savesVenue() {
        venueRepository.save(Venue.create(UUID.randomUUID(), new VenueName("Red room")));

        String name = "Blue name";
        UUID uuid = createVenue.run(name);

        assertThat(venueRepository.findById(uuid))
                .get()
                .extracting(Venue::getName)
                .extracting(VenueName::value)
                .isEqualTo(name);
        assertThat(venueRepository.size()).isEqualTo(2);
    }

    @Test
    void create_blankName_throwsAndSaveNothing() {
        String name = "  ";

        assertThatThrownBy(() -> createVenue.run(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");

        assertThat(venueRepository.size()).isZero();
    }

    static class InMemoryVenueRepository implements VenueRepository {
        private final Map<UUID, Venue> storage;

        InMemoryVenueRepository() {
            this.storage = new HashMap<>();
        }

        @Override
        public Optional<Venue> findById(UUID id) {
            Venue venue = storage.get(id);
            return Optional.ofNullable(venue);
        }

        @Override
        public Optional<Venue> findByName(VenueName name) {
            return storage
                    .values()
                    .stream()
                    .filter((venue -> name.equals(venue.getName())))
                    .findFirst();
        }

        @Override
        public Venue save(Venue venue) {
            storage.put(venue.getId(), venue);
            return venue;
        }


        public int size() {
            return storage.size();
        }
    }
}
