package com.tripplanner.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tripplanner.backend.domain.Place;
import com.tripplanner.backend.domain.Review;
import com.tripplanner.backend.dto.review.CreateReviewRequest;
import com.tripplanner.backend.dto.review.UpdateReviewRequest;
import com.tripplanner.backend.exception.ConflictException;
import com.tripplanner.backend.exception.ForbiddenException;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.exception.ValidationException;
import com.tripplanner.backend.repository.CityRepository;
import com.tripplanner.backend.repository.PlaceRepository;
import com.tripplanner.backend.repository.ReviewRepository;
import com.tripplanner.backend.user.AppUser;
import com.tripplanner.backend.user.Role;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private PlaceRepository placeRepository;
    @Mock private CityRepository cityRepository;
    @InjectMocks private ReviewService reviewService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void userCreatesReviewAndPlaceAverageIsRecalculated() {
        authenticate(2L, Role.USER);
        Place place = place();
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));
        when(reviewRepository.existsByUserIdAndPlaceId(2L, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            review.setId(10L);
            return review;
        });
        when(reviewRepository.calculateAverageRatingByPlaceId(1L)).thenReturn(4.5);

        var response = reviewService.createReview(new CreateReviewRequest(1L, 5, "Great"));

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getUserId()).isEqualTo(2L);
        assertThat(response.getPlaceId()).isEqualTo(1L);
        assertThat(place.getAvgRating()).isEqualTo(4.5);
        verify(placeRepository).save(place);
    }

    @Test
    void userCannotReviewSamePlaceTwice() {
        authenticate(2L, Role.USER);
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place()));
        when(reviewRepository.existsByUserIdAndPlaceId(2L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.createReview(new CreateReviewRequest(1L, 5, "Great")))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Review already exists for this place");
    }

    @Test
    void normalUserCannotFilterReviewsByUserOrCity() {
        authenticate(2L, Role.USER);

        assertThatThrownBy(() -> reviewService.listReviews(null, 3L, null, 0, 20))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Only admins can filter by userId or cityId");
    }

    @Test
    void adminCanFilterReviewsByExistingCity() {
        authenticate(1L, Role.ADMIN);
        when(cityRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findFiltered(org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.eq(1L),
                any())).thenReturn(new PageImpl<>(java.util.List.of()));

        assertThat(reviewService.listReviews(null, null, 1L, 0, 20).getContent()).isEmpty();
    }

    @Test
    void updateRejectsEmptyPatch() {
        authenticate(2L, Role.USER);

        assertThatThrownBy(() -> reviewService.updateReview(10L, new UpdateReviewRequest(null, null)))
                .isInstanceOf(ValidationException.class)
                .hasMessage("At least one of rating or comment must be provided");
    }

    @Test
    void nonAdminUpdatesOnlyOwnReview() {
        authenticate(2L, Role.USER);
        Review review = Review.builder().id(10L).userId(2L).placeId(1L).rating(3).comment("Old").build();
        Place place = place();
        when(reviewRepository.findByIdAndUserId(10L, 2L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.calculateAverageRatingByPlaceId(1L)).thenReturn(5.0);
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place));

        var response = reviewService.updateReview(10L, new UpdateReviewRequest(5, "Updated"));

        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getComment()).isEqualTo("Updated");
        assertThat(place.getAvgRating()).isEqualTo(5.0);
    }

    @Test
    void nonAdminCannotUpdateAnotherUsersReview() {
        authenticate(2L, Role.USER);
        when(reviewRepository.findByIdAndUserId(10L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(10L, new UpdateReviewRequest(5, null)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Review not found");
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
                .isActive(true)
                .avgRating(0.0)
                .build();
    }

    private static void authenticate(Long id, Role role) {
        AppUser user = new AppUser("user" + id, "user" + id + "@example.com", "hash", role);
        user.setId(id);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }
}
