package com.knu.ticketing.infra.web.venue;

import com.knu.ticketing.application.venue.CreateVenue;
import com.knu.ticketing.application.venue.GetVenue;
import com.knu.ticketing.domain.venue.Venue;
import com.knu.ticketing.domain.venue.VenueName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/venues")
class VenueRestApi {

    private final CreateVenue createVenue;
    private final GetVenue getVenue;

    VenueRestApi(CreateVenue createVenue, GetVenue getVenue) {
        this.createVenue = createVenue;
        this.getVenue = getVenue;
    }

    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody CreateVenueRequest request) {
        UUID id = createVenue.run(request.name());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<VenueResponse> get(@PathVariable UUID id) {
        Venue venue = getVenue.run(id);
        return ResponseEntity.ok(VenueResponse.from(venue));
    }

    record CreateVenueRequest(@NotBlank @Size(max = VenueName.MAX_LENGTH) String name) {
    }

    record VenueResponse(UUID id, String name) {
        static VenueResponse from(Venue venue) {
            return new VenueResponse(venue.getId(), venue.getName().value());
        }
    }
}
