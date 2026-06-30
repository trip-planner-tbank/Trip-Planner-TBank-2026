package com.tripplanner.backend.dto.city;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CityRequest(
        @NotBlank
        @Size(max = 50)
        String name,

        @NotBlank
        @Size(max = 50)
        String country
) {
}
