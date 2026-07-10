package com.tripplanner.backend.controller;

import com.tripplanner.backend.dto.geocoding.GeocodingResponse;
import com.tripplanner.backend.service.GeocodingService;
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
public class GeocodingController {

    private final GeocodingService geocodingService;

    @GetMapping("/search")
    public ResponseEntity<GeocodingResponse> search(@RequestParam @NotBlank String q) {
        return ResponseEntity.ok(geocodingService.geocode(q));
    }
}
