package com.tripplanner.backend.controller;

import com.tripplanner.backend.dto.hotel.CreateHotelDetailsRequest;
import com.tripplanner.backend.dto.hotel.HotelDetailsResponse;
import com.tripplanner.backend.service.HotelDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/places/{id}/hotel-details")
@RequiredArgsConstructor
@Tag(name = "Hotel")
public class HotelDetailsController {

    private final HotelDetailsService hotelDetailsService;

    @GetMapping
    @Operation(summary = "Get hotel details", description = "Get hotel details for a specific place (only if placeType = HOTEL).")
    public ResponseEntity<HotelDetailsResponse> getHotelDetails(@PathVariable Long id) {
        return ResponseEntity.ok(hotelDetailsService.getByPlaceId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create hotel details", description = "Create hotel details for a corporate hotel place. ADMIN only.")
    public ResponseEntity<HotelDetailsResponse> createHotelDetails(
            @PathVariable Long id,
            @Valid @RequestBody CreateHotelDetailsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelDetailsService.create(id, request));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update hotel details", description = "Update hotel details for a corporate hotel place. ADMIN only.")
    public ResponseEntity<HotelDetailsResponse> updateHotelDetails(
            @PathVariable Long id,
            @Valid @RequestBody CreateHotelDetailsRequest request) {
        return ResponseEntity.ok(hotelDetailsService.update(id, request));
    }
}
