package com.tripplanner.backend.service;

import com.tripplanner.backend.domain.Review;
import com.tripplanner.backend.domain.Place;
import com.tripplanner.backend.dto.review.CreateReviewRequest;
import com.tripplanner.backend.dto.review.ReviewResponse;
import com.tripplanner.backend.dto.review.UpdateReviewRequest;
import com.tripplanner.backend.exception.ConflictException;
import com.tripplanner.backend.exception.ForbiddenException;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.exception.ValidationException;
import com.tripplanner.backend.repository.PlaceRepository;
import com.tripplanner.backend.repository.ReviewRepository;
import com.tripplanner.backend.repository.CityRepository;
import com.tripplanner.backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final CityRepository cityRepository;

    @Transactional(readOnly = true)
    public Page<ReviewResponse> listReviews(Long placeId, Long userId, Long cityId, int page, int size) {
        if ((userId != null || cityId != null) && !SecurityUtil.isAdmin()) {
            throw new ForbiddenException("Only admins can filter by userId or cityId");
        }

        if (placeId != null) {
            placeRepository.findById(placeId)
                    .orElseThrow(() -> new NotFoundException("Place not found"));
        }
        if (cityId != null && !cityRepository.existsById(cityId)) {
            throw new NotFoundException("City not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return reviewRepository.findFiltered(placeId, userId, cityId, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long id) {
        Review review = findReviewOrThrow(id);
        return mapToResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> listMyReviews(int page, int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return reviewRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new NotFoundException("Place not found"));

        if (reviewRepository.existsByUserIdAndPlaceId(userId, place.getId())) {
            throw new ConflictException("Review already exists for this place");
        }

        Review review = Review.builder()
                .userId(userId)
                .placeId(place.getId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        recalculateAverageRating(place.getId());

        return mapToResponse(saved);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, UpdateReviewRequest request) {
        if (request.getRating() == null && request.getComment() == null) {
            throw new ValidationException("At least one of rating or comment must be provided");
        }

        Review review = SecurityUtil.isAdmin()
                ? findReviewOrThrow(id)
                : reviewRepository.findByIdAndUserId(id, SecurityUtil.getCurrentUserId())
                        .orElseThrow(() -> new NotFoundException("Review not found"));

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        Review saved = reviewRepository.save(review);
        recalculateAverageRating(review.getPlaceId());

        return mapToResponse(saved);
    }

    @Transactional
    public void deleteReview(Long id) {
        Review review = findReviewOrThrow(id);
        reviewRepository.delete(review);
        recalculateAverageRating(review.getPlaceId());
    }

    private Review findReviewOrThrow(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found"));
    }

    private void recalculateAverageRating(Long placeId) {
        Double avg = reviewRepository.calculateAverageRatingByPlaceId(placeId);
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("Place not found"));
        place.setAvgRating(avg != null ? avg : 0.0);
        placeRepository.save(place);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .placeId(review.getPlaceId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
