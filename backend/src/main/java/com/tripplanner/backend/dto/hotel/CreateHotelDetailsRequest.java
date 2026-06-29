package com.tripplanner.backend.dto.hotel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHotelDetailsRequest {

    @NotNull(message = "starRating is required")
    @Min(value = 1, message = "starRating must be between 1 and 5")
    @Max(value = 5, message = "starRating must be between 1 and 5")
    private Integer starRating;

    private String phone;

    private String website;

    @NotNull(message = "roomCount is required")
    @Positive(message = "roomCount must be greater than 0")
    private Integer roomCount;
}
