package com.tripplanner.backend.dto.place;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tripplanner.backend.domain.Place;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlaceWithDistanceResponse(
        Long id,
        Long cityId,
        Long placeTypeId,
        Long createdBy,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String description,
        Boolean isActive,
        Double avgRating,
        Instant createdAt,
        Double distanceKm
) {
    public static PlaceWithDistanceResponse from(Place place, Double distanceKm) {
        return new PlaceWithDistanceResponse(
                place.getId(),
                place.getCityId(),
                place.getPlaceTypeId(),
                place.getCreatedBy(),
                place.getName(),
                place.getAddress(),
                place.getLatitude(),
                place.getLongitude(),
                place.getDescription(),
                place.getIsActive(),
                place.getAvgRating(),
                place.getCreatedAt(),
                distanceKm);
    }
}
