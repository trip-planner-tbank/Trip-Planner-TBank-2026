package com.tripplanner.backend.controller;

import com.tripplanner.backend.dto.review.CreateReviewRequest;
import com.tripplanner.backend.dto.review.ReviewResponse;
import com.tripplanner.backend.dto.review.UpdateReviewRequest;
import com.tripplanner.backend.service.ReviewService;
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
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
@Tag(name = "Review")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Get reviews", description = "Get a list of reviews for place cards and the admin panel.")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @RequestParam(required = false) Long placeId,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(reviewService.listReviews(placeId, userId, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review", description = "Get a specific review by id.")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReview(id));
    }

    @PostMapping
    @Operation(summary = "Create review", description = "Create a new review for a place. One user can create only one review per place.")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update review", description = "Update an existing review. Owners can update their own reviews, admins can update any review.")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete review", description = "Delete a review. ADMIN only.")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
