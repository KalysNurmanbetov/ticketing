package com.knu.ticketing.domain.venue;

import java.util.Locale;
import java.util.Objects;

public record VenueName(String value) {
    public VenueName {
        Objects.requireNonNull(value, "venue name should be defined");
        value = value
                .strip()
                .replaceAll("\\s+", " ");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("venue name should not be empty");
        }
    }

    public String normalized() {
        return value.toLowerCase(Locale.ROOT);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        VenueName venueName = (VenueName) o;
        return Objects.equals(normalized(), venueName.normalized());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(normalized());
    }

}
