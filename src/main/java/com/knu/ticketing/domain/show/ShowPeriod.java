package com.knu.ticketing.domain.show;

import java.time.Instant;
import java.util.Objects;

public record ShowPeriod(Instant start, Instant end) {
    public ShowPeriod {
        Objects.requireNonNull(start, "start should be defined");
        Objects.requireNonNull(end, "end should be defined");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("invalid show period: end should be after start");
        }
    }
}