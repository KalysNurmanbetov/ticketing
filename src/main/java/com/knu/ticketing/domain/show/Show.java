package com.knu.ticketing.domain.show;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Show {
    private UUID id;
    private UUID venueId;
    private ShowStatus status;
    private String title;
    private ShowPeriod period;

    private Show() {
    }

    public static Show introduce(UUID id, String title) {
        Objects.requireNonNull(id, "id should be defined");
        requireTitle(title);

        Show show = new Show();
        show.id = id;
        show.title = title;
        show.status = ShowStatus.DRAFT;
        return show;
    }

    public void assignVenue(UUID venueId) {
        Objects.requireNonNull(venueId, "venueId should be defined");
        requireDraftStatus();
        this.venueId = venueId;
    }

    public void schedule(ShowPeriod period) {
        Objects.requireNonNull(period, "period should be defined");
        requireDraftStatus();
        this.period = period;
    }

    public void changeTitle(String title) {
        requireTitle(title);
        requireDraftStatus();
        this.title = title;
    }

    public boolean isStarted(Instant now) {
        Objects.requireNonNull(now, "'now' should be defined");
        if (period == null) {
            throw new IllegalStateException("show has no period yet");
        }
        return !now.isBefore(period.start());
    }

    public void publish(Instant now) {
        requireDraftStatus();
        if (venueId == null) {
            throw new IllegalStateException("venue is not assigned");
        }
        if (period == null) {
            throw new IllegalStateException("show is not scheduled");
        }
        if (isStarted(now)) {
            throw new IllegalStateException("start has already passed");
        }

        this.status = ShowStatus.PUBLISHED;
    }

    public void cancel(Instant now) {
        if (ShowStatus.CANCELED == status) {
            throw new IllegalStateException("show is already canceled");
        }
        if (ShowStatus.DRAFT != status && isStarted(now)) {
            throw new IllegalStateException("start has already passed");
        }
        this.status = ShowStatus.CANCELED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getVenueId() {
        return venueId;
    }

    public ShowStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public ShowPeriod getPeriod() {
        return period;
    }


    private void requireDraftStatus() {
        if (ShowStatus.DRAFT != status) {
            throw new IllegalStateException("show is not in draft");
        }
    }

    private static void requireTitle(String title) {
        Objects.requireNonNull(title, "title should be defined");
        if (title.isBlank()) {
            throw new IllegalArgumentException("title should not be empty");
        }
    }
}
