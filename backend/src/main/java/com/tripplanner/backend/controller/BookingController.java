package com.tripplanner.backend.controller;

import com.tripplanner.backend.domain.BookingStatus;
import com.tripplanner.backend.dto.booking.*;
import com.tripplanner.backend.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
@Tag(name = "Booking")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "Get bookings", description = "Get a list of bookings. Regular users receive only their own bookings. ADMIN users receive all bookings and can filter by user.")
    public ResponseEntity<Page<BookingResponse>> getBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(bookingService.listBookings(status, userId, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking", description = "Get a specific booking by id. Accessible by the booking owner or ADMIN.")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    @PostMapping
    @Operation(summary = "Create booking", description = "Create a new booking for a hotel place.")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update booking", description = "Update an existing booking. Only the booking owner can update it.")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingRequest request) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update booking status", description = "Allows an admin to change the status of a booking (confirm or cancel).")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingStatusRequest request) {
        return ResponseEntity.ok(bookingService.updateStatus(id, request));
    }
}
