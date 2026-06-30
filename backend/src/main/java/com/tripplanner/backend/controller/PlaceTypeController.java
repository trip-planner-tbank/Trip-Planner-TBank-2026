package com.tripplanner.backend.controller;

import com.tripplanner.backend.domain.PlaceType;
import com.tripplanner.backend.repository.PlaceTypeRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/place-types")
@RequiredArgsConstructor
@Tag(name = "Place Type")
public class PlaceTypeController {

    private final PlaceTypeRepository placeTypeRepository;

    @GetMapping
    public ResponseEntity<List<PlaceType>> getPlaceTypes() {
        return ResponseEntity.ok(placeTypeRepository.findAll());
    }
}
