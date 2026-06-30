package com.tripplanner.backend.controller;

import com.tripplanner.backend.domain.Place;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.repository.CityRepository;
import com.tripplanner.backend.repository.PlaceRepository;
import com.tripplanner.backend.repository.PlaceTypeRepository;
import com.tripplanner.backend.security.SecurityUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceRepository placeRepository;
    private final CityRepository cityRepository;
    private final PlaceTypeRepository placeTypeRepository;

    @GetMapping
    public ResponseEntity<List<Place>> getAllPlaces() {
        return ResponseEntity.ok(placeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlace(@PathVariable Long id) {
        return ResponseEntity.ok(findPlaceOrThrow(id));
    }

    @PostMapping
    public ResponseEntity<Place> createPlace(@Valid @RequestBody CreatePlaceRequest request) {
        validateReferences(request.cityId(), request.placeTypeId());
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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Place> updatePlace(
            @PathVariable Long id,
            @Valid @RequestBody CreatePlaceRequest request) {
        validateReferences(request.cityId(), request.placeTypeId());
        Place place = findPlaceOrThrow(id);
        place.setCityId(request.cityId());
        place.setPlaceTypeId(request.placeTypeId());
        place.setName(request.name());
        place.setAddress(request.address());
        place.setLatitude(request.latitude());
        place.setLongitude(request.longitude());
        place.setDescription(request.description());
        return ResponseEntity.ok(placeRepository.save(place));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        Place place = findPlaceOrThrow(id);
        placeRepository.delete(place);
        return ResponseEntity.noContent().build();
    }

    private Place findPlaceOrThrow(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Place not found"));
    }

    private void validateReferences(Long cityId, Long placeTypeId) {
        if (!cityRepository.existsById(cityId)) {
            throw new NotFoundException("City not found");
        }
        if (!placeTypeRepository.existsById(placeTypeId)) {
            throw new NotFoundException("Place type not found");
        }
    }

    public record CreatePlaceRequest(
            @NotNull Long cityId,
            @NotNull Long placeTypeId,
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Size(max = 255) String address,
            @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @Size(max = 1000) String description
    ) {
    }
}
