package com.knu.ticketing.domain.venue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class VenueTest {

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "",
                    "  ",
                    "\n",
                    "\t"
            }
    )
    void create_emptyName_throws(String name) {
        assertThatThrownBy(() -> Venue.create(UUID.randomUUID(), name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void create_success() {
        UUID id = UUID.randomUUID();
        String name = "Red room";
        Venue venue = Venue.create(id, name);

        assertThat(venue.getId()).isEqualTo(id);
        assertThat(venue.getName()).isEqualTo(name);
    }
}
