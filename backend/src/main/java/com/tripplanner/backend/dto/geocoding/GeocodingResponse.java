package com.tripplanner.backend.dto.geocoding;

public record GeocodingResponse(
        String displayName,
        Double latitude,
        Double longitude
) {
}
