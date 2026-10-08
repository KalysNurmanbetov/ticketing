package com.knu.ticketing.infra.repositories.venue;

import com.knu.ticketing.domain.venue.Venue;
import com.knu.ticketing.domain.venue.VenueName;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "venues")
public class VenueModel {

    @Id
    private UUID id;

    private String name;

    protected VenueModel() {
    }

    private VenueModel(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public static VenueModel from(Venue domain) {
        UUID id = domain.getId();
        String name = domain
                .getName()
                .value();
        return new VenueModel(id, name);
    }

    public Venue toDomain() {
        return Venue.reconstitute(id, new VenueName(name));
    }
}
