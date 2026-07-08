package com.tripplanner.backend.controller;

import com.tripplanner.backend.domain.Office;
import com.tripplanner.backend.dto.office.OfficeRequest;
import com.tripplanner.backend.service.OfficeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/offices")
@RequiredArgsConstructor
@Tag(name = "Office")
public class OfficeController {

    private final OfficeService officeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Office> createOffice(@Valid @RequestBody OfficeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(officeService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<Office>> getOffices(@RequestParam(required = false) Long cityId) {
        return ResponseEntity.ok(officeService.list(cityId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Office> getOffice(@PathVariable Long id) {
        return ResponseEntity.ok(officeService.get(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Office> updateOffice(@PathVariable Long id, @Valid @RequestBody OfficeRequest request) {
        return ResponseEntity.ok(officeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
        officeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
