package com.tripplanner.backend.controller;

import com.tripplanner.backend.domain.Place;
import com.tripplanner.backend.dto.place.PlaceWithDistanceResponse;
import com.tripplanner.backend.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
@Validated
@Tag(name = "Place")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    @Operation(
            summary = "Get places",
            description = "Get a list of active places with optional filtering by city, type, and distance from a reference point.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "City, office, or reference place not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<List<PlaceWithDistanceResponse>> getAllPlaces(
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) Long placeTypeId,
            @RequestParam(required = false) Long officeId,
            @RequestParam(required = false) Long referencePlaceId,
            @RequestParam(required = false) @DecimalMin("0.0") Double maxDistanceKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(placeService.list(
                cityId, placeTypeId, officeId, referencePlaceId, maxDistanceKm, page, size));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get place",
            description = "Get a specific place by id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Place not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Place> getPlace(@PathVariable Long id) {
        return ResponseEntity.ok(placeService.get(id));
    }

    @PostMapping
    @Operation(
            summary = "Create place",
            description = "Create a new place. HOTEL type requires ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden (HOTEL type not allowed for non-ADMIN)"),
                    @ApiResponse(responseCode = "404", description = "City or place type not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Place> createPlace(@Valid @RequestBody CreatePlaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(placeService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update place",
            description = "Update a specific place by id. ADMIN only.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden (not ADMIN role)"),
                    @ApiResponse(responseCode = "404", description = "Place not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Place> updatePlace(
            @PathVariable Long id,
            @Valid @RequestBody CreatePlaceRequest request) {
        return ResponseEntity.ok(placeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete place",
            description = "Delete a specific place by id. ADMIN only.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "400", description = "Invalid id format"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden (not ADMIN role)"),
                    @ApiResponse(responseCode = "404", description = "Place not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        placeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/nearby-places")
    @Operation(
            summary = "Get places near place",
            description = "Get all active places near the selected place, sorted by distance (ascending). The reference place itself is excluded.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Place not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<List<PlaceWithDistanceResponse>> getNearbyPlaces(
            @PathVariable Long id,
            @RequestParam(required = false) Long placeTypeId,
            @RequestParam(required = false) @DecimalMin("0.0") Double maxDistanceKm) {
        return ResponseEntity.ok(placeService.nearbyPlace(id, placeTypeId, maxDistanceKm));
    }

    public record CreatePlaceRequest(
            @NotNull Long cityId,
            @NotNull Long placeTypeId,
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Size(max = 255) String address,
            @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @Size(max = 1000) String description
    ) {
    }
}
