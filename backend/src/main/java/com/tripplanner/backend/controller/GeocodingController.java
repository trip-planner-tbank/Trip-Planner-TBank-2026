package com.tripplanner.backend.controller;

import com.tripplanner.backend.dto.geocoding.GeocodingResponse;
import com.tripplanner.backend.service.GeocodingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/geocoding")
@RequiredArgsConstructor
@Validated
@Tag(name = "Geocoding")
public class GeocodingController {

    private final GeocodingService geocodingService;

    @GetMapping("/search")
    @Operation(
            summary = "Geocode an address",
            description = "Resolve an address to WGS84 coordinates using the configured Nominatim service.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Best matching address"),
                    @ApiResponse(responseCode = "400", description = "Blank address or no matching address"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "502", description = "Geocoding provider unavailable or returned invalid data")
            }
    )
    public ResponseEntity<GeocodingResponse> search(@RequestParam @NotBlank String q) {
        return ResponseEntity.ok(geocodingService.geocode(q));
    }
}
