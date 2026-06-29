package com.tripplanner.backend.service;

import com.tripplanner.backend.domain.Wishlist;
import com.tripplanner.backend.dto.wishlist.CreateWishlistRequest;
import com.tripplanner.backend.dto.wishlist.PlaceSummary;
import com.tripplanner.backend.dto.wishlist.WishlistEntryResponse;
import com.tripplanner.backend.exception.ConflictException;
import com.tripplanner.backend.exception.ForbiddenException;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.mock.domain.Place;
import com.tripplanner.backend.mock.repository.PlaceRepository;
import com.tripplanner.backend.mock.security.SecurityUtil;
import com.tripplanner.backend.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final PlaceRepository placeRepository;

    @Transactional(readOnly = true)
    public List<WishlistEntryResponse> getCurrentUserWishlist() {
        Long userId = SecurityUtil.getCurrentUserId();
        return wishlistRepository.findByUserIdOrderByAddedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public WishlistEntryResponse addToWishlist(CreateWishlistRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new NotFoundException("Place not found"));

        if (wishlistRepository.existsByUserIdAndPlaceId(userId, place.getId())) {
            throw new ConflictException("Place already in wishlist");
        }

        Wishlist entry = Wishlist.builder()
                .userId(userId)
                .placeId(place.getId())
                .build();

        return mapToResponse(wishlistRepository.save(entry));
    }

    @Transactional
    public void removeFromWishlist(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        Wishlist entry = wishlistRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Wishlist entry not found"));
        wishlistRepository.delete(entry);
    }

    private WishlistEntryResponse mapToResponse(Wishlist entry) {
        Place place = placeRepository.findById(entry.getPlaceId())
                .orElseThrow(() -> new NotFoundException("Place not found"));

        return WishlistEntryResponse.builder()
                .id(entry.getId())
                .userId(entry.getUserId())
                .placeId(entry.getPlaceId())
                .addedAt(entry.getAddedAt())
                .place(PlaceSummary.builder()
                        .id(place.getId())
                        .cityId(place.getCityId())
                        .placeTypeId(place.getPlaceTypeId())
                        .name(place.getName())
                        .address(place.getAddress())
                        .latitude(place.getLatitude())
                        .longitude(place.getLongitude())
                        .avgRating(place.getAvgRating())
                        .isActive(place.getIsActive())
                        .build())
                .build();
    }
}
