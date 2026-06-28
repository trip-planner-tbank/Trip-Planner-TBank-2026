package com.tripplanner.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hotel_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false, unique = true)
    private Long placeId;

    @Column(name = "star_rating", nullable = false)
    private Integer starRating;

    @Column(length = 50)
    private String phone;

    @Column(length = 255)
    private String website;

    @Column(name = "room_count", nullable = false)
    private Integer roomCount;
}
