package com.tripplanner.backend.dto.wishlist;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWishlistRequest {

    @NotNull(message = "placeId is required")
    private Long placeId;
}
