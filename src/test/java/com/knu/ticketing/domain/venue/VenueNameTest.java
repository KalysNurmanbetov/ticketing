package com.knu.ticketing.domain.venue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VenueNameTest {

    private static final String NAME = "Red room";

    @Test
    void new_null_throws() {
        assertThatThrownBy(() -> new VenueName(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("venue name");
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "",
                    "  ",
                    "\n",
                    "\t"
            }
    )
    void new_blankValue_throws(String input) {
        assertThatThrownBy(() -> new VenueName(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("venue name");
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "  Red room  ",
                    "Red   room",
                    "Red\troom",
                    "Red\nroom",
                    NAME
            }
    )
    void new_extraWhitespace_normalized(String input) {
        assertThat(new VenueName(input).value()).isEqualTo(NAME);
    }

    @Test
    void new_mixedCase_preservedInValue() {
        String name = "RED ROOM";
        assertThat(new VenueName(name).value()).isEqualTo(name);
    }

    @Test
    void normalized_mixedCase_lowercased() {
        assertThat(new VenueName(NAME).normalized()).isEqualTo("red room");
    }

    static Stream<String> equalsNames() {
        return Stream.of("red room", "RED ROOM", "Red Room", "Red   room", NAME);
    }


    @ParameterizedTest
    @MethodSource("equalsNames")
    void equals_differentCaseAndSpacing_true(String input) {
        assertThat(new VenueName(input)).isEqualTo(new VenueName(NAME));
    }

    @ParameterizedTest
    @MethodSource("equalsNames")
    void hashCode_equalNames_same(String input) {
        assertThat(new VenueName(input).hashCode()).isEqualTo(new VenueName(NAME).hashCode());

    }

    @Test
    void equals_differentNames_false() {
        assertThat(new VenueName("Red rooms")).isNotEqualTo(new VenueName(NAME));
    }
}
