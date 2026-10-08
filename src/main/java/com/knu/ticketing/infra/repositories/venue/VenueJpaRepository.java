package com.knu.ticketing.infra.repositories.venue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface VenueJpaRepository extends JpaRepository<VenueModel, UUID> {

    @Query("SELECT v FROM VenueModel v where lower(v.name) = :normalizedName")
    Optional<VenueModel> findByNormalizedName(@Param("normalizedName") String normalizedName);
}
