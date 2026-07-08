package com.tripplanner.backend.dto.wishlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceSummary {

    private Long id;
    private Long cityId;
    private Long placeTypeId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double avgRating;
    private Boolean isActive;
}
