package com.tripplanner.backend.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDetailsResponse {

    private Long id;
    private Long placeId;
    private Integer starRating;
    private String phone;
    private String website;
    private Integer roomCount;
}
