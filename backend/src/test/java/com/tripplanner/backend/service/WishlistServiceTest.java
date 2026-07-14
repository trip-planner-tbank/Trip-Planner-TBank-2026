package com.tripplanner.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tripplanner.backend.domain.Place;
import com.tripplanner.backend.domain.Wishlist;
import com.tripplanner.backend.dto.wishlist.CreateWishlistRequest;
import com.tripplanner.backend.exception.ConflictException;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.repository.PlaceRepository;
import com.tripplanner.backend.repository.WishlistRepository;
import com.tripplanner.backend.user.AppUser;
import com.tripplanner.backend.user.Role;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock private WishlistRepository wishlistRepository;
    @Mock private PlaceRepository placeRepository;
    @InjectMocks private WishlistService wishlistService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addsPlaceToCurrentUsersWishlist() {
        authenticate(2L);
        Place place = place();
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));
        when(wishlistRepository.existsByUserIdAndPlaceId(2L, 1L)).thenReturn(false);
        when(wishlistRepository.save(org.mockito.ArgumentMatchers.any(Wishlist.class)))
                .thenAnswer(invocation -> {
                    Wishlist entry = invocation.getArgument(0);
                    entry.setId(10L);
                    return entry;
                });

        var response = wishlistService.addToWishlist(new CreateWishlistRequest(1L));

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getUserId()).isEqualTo(2L);
        assertThat(response.getPlace().getName()).isEqualTo("Cafe");
    }

    @Test
    void rejectsDuplicateWishlistEntry() {
        authenticate(2L);
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place()));
        when(wishlistRepository.existsByUserIdAndPlaceId(2L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> wishlistService.addToWishlist(new CreateWishlistRequest(1L)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Place already in wishlist");
    }

    @Test
    void returnsCurrentUsersWishlistWithPlaceSummary() {
        authenticate(2L);
        Wishlist entry = Wishlist.builder()
                .id(10L)
                .userId(2L)
                .placeId(1L)
                .addedAt(OffsetDateTime.now())
                .build();
        when(wishlistRepository.findByUserIdOrderByAddedAtDesc(2L)).thenReturn(List.of(entry));
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place()));

        var result = wishlistService.getCurrentUserWishlist();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPlace().getId()).isEqualTo(1L);
    }

    @Test
    void removesOnlyCurrentUsersWishlistEntry() {
        authenticate(2L);
        Wishlist entry = Wishlist.builder().id(10L).userId(2L).placeId(1L).build();
        when(wishlistRepository.findByIdAndUserId(10L, 2L)).thenReturn(Optional.of(entry));

        wishlistService.removeFromWishlist(10L);

        verify(wishlistRepository).delete(entry);
    }

    @Test
    void removeRejectsMissingOrOtherUsersEntry() {
        authenticate(2L);
        when(wishlistRepository.findByIdAndUserId(10L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishlistService.removeFromWishlist(10L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Wishlist entry not found");
    }

    private static Place place() {
        return Place.builder()
                .id(1L)
                .cityId(1L)
                .placeTypeId(2L)
                .createdBy(1L)
                .name("Cafe")
                .address("Address")
                .latitude(0.0)
                .longitude(0.0)
                .avgRating(4.0)
                .isActive(true)
                .build();
    }

    private static void authenticate(Long id) {
        AppUser user = new AppUser("user" + id, "user" + id + "@example.com", "hash", Role.USER);
        user.setId(id);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }
}
