package com.knu.ticketing.infra.web.venue;

import com.knu.ticketing.application.venue.CreateVenue;
import com.knu.ticketing.application.venue.GetVenue;
import com.knu.ticketing.application.venue.errors.VenueAlreadyExistsException;
import com.knu.ticketing.application.venue.errors.VenueNotFoundException;
import com.knu.ticketing.domain.venue.Venue;
import com.knu.ticketing.domain.venue.VenueName;
import com.knu.ticketing.infra.repositories.venue.VenueRepositoryAdapter;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VenueRestApi.class)
public class VenueRestApiTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    CreateVenue createVenue;

    @MockitoBean
    GetVenue getVenue;

    @Test
    void create_validName_returns201WithLocation() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "Red Room";

        when(createVenue.run(name)).thenReturn(id);

        mvc.perform(post("/venues").contentType(MediaType.APPLICATION_JSON).content(String.format(
                """
                        {"name":"%s"}
                        """, name
        ))).andExpect(status().isCreated()).andExpect(header().string("Location", endsWith("/venues/" + id)));
    }

    @Test
    void create_emptyName_returns400() throws Exception {
        mvc
                .perform(post("/venues").contentType(MediaType.APPLICATION_JSON).content("""
                                                                                                 {"name":""}
                                                                                                 """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(createVenue, never()).run(any());
    }


    @Test
    void create_tooLong_returns400() throws Exception {
        String name = "R".repeat(VenueName.MAX_LENGTH + 1);

        mvc
                .perform(post("/venues").contentType(MediaType.APPLICATION_JSON).content(String.format(
                        """
                                {"name":"%s"}
                                """, name
                )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(createVenue, never()).run(any());
    }


    @Test
    void create_duplicateName_returns409() throws Exception {
        String name = "Red Room";

        when(createVenue.run(name)).thenThrow(new VenueAlreadyExistsException(new VenueName(name)));

        mvc
                .perform(post("/venues").contentType(MediaType.APPLICATION_JSON).content(String.format(
                        """
                                {"name":"%s"}
                                """, name
                )))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value(containsString(name)));
    }

    @Test
    void create_raceOfUniqueName_returns409() throws Exception {
        String name = "Red Room";

        when(createVenue.run(name)).thenThrow(violationOf(VenueRepositoryAdapter.VENUE_NAME_KEY_INDEX));

        mvc
                .perform(post("/venues").contentType(MediaType.APPLICATION_JSON).content(String.format(
                        """
                                {"name":"%s"}
                                """, name
                )))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void create_otherIntegrityViolation_returns500() throws Exception {
        String name = "Red Room";

        when(createVenue.run(name)).thenThrow(violationOf("other_key"));

        mvc
                .perform(post("/venues").contentType(MediaType.APPLICATION_JSON).content(String.format(
                        """
                                {"name":"%s"}
                                """, name
                )))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value(not(containsString("secret"))))
                .andExpect(jsonPath("$.detail").value(not(containsString("other_key"))));
    }

    @Test
    void get_existing_returnsJson() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "Red Room";

        when(getVenue.run(id)).thenReturn(Venue.reconstitute(id, new VenueName(name)));

        mvc
                .perform(get("/venues/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void get_nonExisting_returns404() throws Exception {
        UUID id = UUID.randomUUID();

        when(getVenue.run(id)).thenThrow(new VenueNotFoundException(id));

        mvc
                .perform(get("/venues/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(containsString(id.toString())));
    }

    @Test
    void get_InvalidId_returns400() throws Exception {
        mvc
                .perform(get("/venues/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(getVenue, never()).run(any());
    }

    private static DataIntegrityViolationException violationOf(String constraintName) {
        var cause = new ConstraintViolationException(
                "duplicated key value violates unique constraint",
                new SQLException("ERROR: secret internal details"),
                constraintName
        );
        return new DataIntegrityViolationException("could not execute statement", cause);
    }
}
