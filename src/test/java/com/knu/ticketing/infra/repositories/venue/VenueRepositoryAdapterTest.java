package com.knu.ticketing.infra.repositories.venue;

import com.knu.ticketing.domain.venue.Venue;
import com.knu.ticketing.domain.venue.VenueName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Testcontainers
@Import(VenueRepositoryAdapter.class)
public class VenueRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer container = new PostgreSQLContainer("postgres:17");

    @Autowired
    VenueRepositoryAdapter repository;

    @Autowired
    TestEntityManager em;

    @Autowired
    VenueJpaRepository jpa;

    @Test
    void save_thenFindById_returnsSameVenue() {
        VenueName name = new VenueName("Red Room");
        Venue venue = Venue.create(UUID.randomUUID(), name);
        repository.save(venue);
        em.flush();
        em.clear();

        assertThat(repository.findById(venue.getId()))
                .get()
                .extracting(Venue::getName)
                .isEqualTo(name);
    }

    @Test
    void save_duplicateVenue_throws() {

        Venue venue1 = Venue.create(UUID.randomUUID(), new VenueName("Red Room"));
        Venue venue2 = Venue.create(UUID.randomUUID(), new VenueName("red room"));
        repository.save(venue1);


        assertThatThrownBy(() -> {
            repository.save(venue2);
            jpa.flush();
            em.clear();
        })
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("venues_name_key");

    }

    @Test
    void findByName_withDifferentRegister_returnsSameVenue() {
        VenueName name = new VenueName("Red Room");
        Venue venue = Venue.create(UUID.randomUUID(), name);
        repository.save(venue);
        em.flush();
        em.clear();

        assertThat(repository.findByName(new VenueName("RED ROOM")))
                .get()
                .extracting(Venue::getId)
                .isEqualTo(venue.getId());
    }

    @Test
    void findByName_withDifferentName_returnsSameVenue() {
        VenueName name = new VenueName("Red Room");
        Venue venue = Venue.create(UUID.randomUUID(), name);
        repository.save(venue);
        em.flush();
        em.clear();

        assertThat(repository.findByName(new VenueName("RED ROOMS"))).isEmpty();
    }
}
