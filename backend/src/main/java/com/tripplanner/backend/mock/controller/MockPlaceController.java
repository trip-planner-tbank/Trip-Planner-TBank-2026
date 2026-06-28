package com.tripplanner.backend.mock.controller;

import com.tripplanner.backend.mock.domain.Place;
import com.tripplanner.backend.mock.repository.PlaceRepository;
import com.tripplanner.backend.mock.security.SecurityUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Replace with the real PlaceController once Developer 1 delivers it.

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class MockPlaceController {

    private final PlaceRepository placeRepository;

    @GetMapping
    public ResponseEntity<List<Place>> getAllPlaces() {
        return ResponseEntity.ok(placeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlace(@PathVariable Long id) {
        return placeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Place> createPlace(@Valid @RequestBody CreatePlaceRequest request) {
        Place place = Place.builder()
                .cityId(request.cityId())
                .placeTypeId(request.placeTypeId())
                .createdBy(SecurityUtil.getCurrentUserId())
                .name(request.name())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .description(request.description())
                .isActive(true)
                .avgRating(0.0)
                .build();

        Place saved = placeRepository.save(place);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    public record CreatePlaceRequest(
            @NotNull Long cityId,
            @NotNull Long placeTypeId,
            @NotBlank String name,
            @NotBlank String address,
            @NotNull Double latitude,
            @NotNull Double longitude,
            String description
    ) {
    }
}
