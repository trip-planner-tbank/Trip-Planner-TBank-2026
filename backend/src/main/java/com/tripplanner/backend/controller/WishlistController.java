package com.tripplanner.backend.controller;

import com.tripplanner.backend.dto.wishlist.CreateWishlistRequest;
import com.tripplanner.backend.dto.wishlist.WishlistEntryResponse;
import com.tripplanner.backend.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlists")
@RequiredArgsConstructor
@Tag(name = "Wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get wishlist", description = "Get the current user's wishlist.")
    public ResponseEntity<List<WishlistEntryResponse>> getWishlist() {
        return ResponseEntity.ok(wishlistService.getCurrentUserWishlist());
    }

    @PostMapping
    @Operation(summary = "Add to wishlist", description = "Add a place to the user's wishlist. Each user can add a place only once.")
    public ResponseEntity<WishlistEntryResponse> addToWishlist(@Valid @RequestBody CreateWishlistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistService.addToWishlist(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove from wishlist", description = "Remove a place from the current user's wishlist by wishlist entry id.")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long id) {
        wishlistService.removeFromWishlist(id);
        return ResponseEntity.noContent().build();
    }
}
