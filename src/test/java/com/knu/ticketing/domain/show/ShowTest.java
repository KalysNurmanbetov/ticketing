package com.knu.ticketing.domain.show;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShowTest {

    private static final Instant START = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant END = START.plus(Duration.ofDays(30));
    private static final ShowPeriod SHOW_PERIOD = new ShowPeriod(START, END);
    private static final Instant BEFORE_START = START.minus(Duration.ofHours(1));

    private Show createdValidDraftShow() {
        Show show = Show.introduce(UUID.randomUUID(), "Test");
        show.assignVenue(UUID.randomUUID());
        show.schedule(SHOW_PERIOD);
        return show;
    }


    @Test
    void introduce_success() {
        String title = "JOOQ new methods";
        Show show = Show.introduce(UUID.randomUUID(), title);
        assertThat(show.getStatus()).isEqualTo(ShowStatus.DRAFT);
        assertThat(show.getTitle()).isEqualTo(title);
        assertThat(show.getPeriod()).isEqualTo(null);
        assertThat(show.getVenueId()).isEqualTo(null);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "",
                    "   ",
                    "\n",
                    "\t"
            }
    )
    void introduce_blankTitle_throws(String input) {
        assertThatThrownBy(() -> Show.introduce(UUID.randomUUID(), input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("title");
    }

    @Test
    void assignVenue_notDraft_throws() {
        Show show = Show.introduce(UUID.randomUUID(), "Test");
        show.cancel(BEFORE_START);
        assertThatThrownBy(() -> show.assignVenue(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("draft");
    }

    @Test
    void schedule_notDraft_throws() {
        Show show = Show.introduce(UUID.randomUUID(), "Test");
        show.cancel(BEFORE_START);
        assertThatThrownBy(() -> show.schedule(SHOW_PERIOD))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("draft");
    }

    @Test
    void changeTitle_notDraft_throws() {
        Show show = Show.introduce(UUID.randomUUID(), "Test");
        show.cancel(BEFORE_START);
        assertThatThrownBy(() -> show.changeTitle("Other test"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("draft");
    }

    static Stream<Arguments> isStartedCases() {
        return Stream.of(
                Arguments.of(START.minus(Duration.ofDays(10)), false),
                Arguments.of(START.minus(Duration.ofSeconds(1)), false),
                Arguments.of(START, true),
                Arguments.of(START.plus(Duration.ofDays(10)), true)
        );
    }

    @ParameterizedTest
    @MethodSource("isStartedCases")
    void isStarted_dependsOnStartPeriod(Instant now, boolean expected) {
        Show show = Show.introduce(UUID.randomUUID(), "Test");
        show.schedule(SHOW_PERIOD);

        assertThat(show.isStarted(now)).isEqualTo(expected);
    }

    @Test
    void isStarted_noPeriod_throws() {
        Show show = Show.introduce(UUID.randomUUID(), "Test");
        assertThatThrownBy(() -> show.isStarted(BEFORE_START))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("period");
    }

    @Test
    void publish_notDraft_throws() {
        Show show = createdValidDraftShow();
        show.cancel(BEFORE_START);

        assertThatThrownBy(() -> show.publish(BEFORE_START))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("draft");

    }

    @Test
    void publish_noVenue_throws() {
        Show show = Show.introduce(UUID.randomUUID(), "Test");
        show.schedule(SHOW_PERIOD);

        assertThatThrownBy(() -> show.publish(BEFORE_START))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("venue");

    }

    @Test
    void publish_notScheduled_throws() {
        Show show = Show.introduce(UUID.randomUUID(), "Test");
        show.assignVenue(UUID.randomUUID());

        assertThatThrownBy(() -> show.publish(BEFORE_START))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("scheduled");

    }

    @Test
    void publish_alreadyStarted_throws() {
        Show show = createdValidDraftShow();

        assertThatThrownBy(() -> show.publish(START)).isInstanceOf(IllegalStateException.class).hasMessageContaining(
                "start");

    }

    @Test
    void publish_draft_success() {
        Show show = createdValidDraftShow();

        show.publish(BEFORE_START);

        assertThat(show.getStatus()).isEqualTo(ShowStatus.PUBLISHED);
    }

    @Test
    void cancel_alreadyCanceled_throws() {
        Show show = createdValidDraftShow();

        show.cancel(BEFORE_START);
        assertThatThrownBy(() -> show.cancel(BEFORE_START))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("canceled");
    }

    @Test
    void cancel_startedPublished_throws() {
        Show show = createdValidDraftShow();

        show.publish(BEFORE_START);

        assertThatThrownBy(() -> show.cancel(START)).isInstanceOf(IllegalStateException.class).hasMessageContaining(
                "start");
    }

    @Test
    void cancel_notStartedPublished_success() {
        Show show = createdValidDraftShow();

        show.publish(BEFORE_START);
        show.cancel(BEFORE_START);

        assertThat(show.getStatus()).isEqualTo(ShowStatus.CANCELED);
    }

    static Stream<Instant> cancelDraftCases() {
        return Stream.of(START.minus(Duration.ofSeconds(1)), START, START.plus(Duration.ofSeconds(1)));
    }

    @ParameterizedTest
    @MethodSource("cancelDraftCases")
    void cancel_draft_success(Instant now) {
        Show show = createdValidDraftShow();

        show.cancel(now);

        assertThat(show.getStatus()).isEqualTo(ShowStatus.CANCELED);
    }


}
