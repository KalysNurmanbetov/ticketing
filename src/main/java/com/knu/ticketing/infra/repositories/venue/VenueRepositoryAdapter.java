package com.knu.ticketing.infra.repositories.venue;

import com.knu.ticketing.domain.venue.Venue;
import com.knu.ticketing.domain.venue.VenueName;
import com.knu.ticketing.domain.venue.VenueRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public class VenueRepositoryAdapter implements VenueRepository {

    private final VenueJpaRepository jpa;

    VenueRepositoryAdapter(VenueJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Venue> findById(UUID id) {
        return jpa
                .findById(id)
                .map(VenueModel::toDomain);
    }

    @Override
    public Optional<Venue> findByName(VenueName name) {
        return jpa
                .findByNormalizedName(name.normalized())
                .map(VenueModel::toDomain);
    }

    @Override
    public Venue save(Venue venue) {
        return jpa
                .save(VenueModel.from(venue))
                .toDomain();
    }
}
