package com.knu.ticketing.domain.venue;

import java.util.Objects;
import java.util.UUID;

public class Venue {
    private final UUID id;
    private final VenueName name;

    private Venue(UUID id, VenueName name) {
        this.id = id;
        this.name = name;
    }

    public static Venue create(UUID id, VenueName name) {
        Objects.requireNonNull(id, "id should be defined");
        Objects.requireNonNull(name, "name should be defined");
        return new Venue(id, name);
    }

    public static Venue reconstitute(UUID id, VenueName name) {
        Objects.requireNonNull(id, "id should be defined");
        Objects.requireNonNull(name, "name should be defined");
        return new Venue(id, name);
    }

    public UUID getId() {
        return id;
    }

    public VenueName getName() {
        return name;
    }


}
