package com.knu.ticketing.domain.venue;

import java.util.Objects;
import java.util.UUID;

public class Venue {
    private UUID id;
    private String name;

    private Venue() {}

    private Venue(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Venue create(UUID id, String name) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        if (name.isBlank()) {
            throw new IllegalArgumentException("name should not be empty");
        }
        return new Venue(id, name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
