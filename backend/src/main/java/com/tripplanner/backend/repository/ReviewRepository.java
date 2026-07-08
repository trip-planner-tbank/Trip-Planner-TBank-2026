package com.tripplanner.backend.repository;

import com.tripplanner.backend.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndPlaceId(Long userId, Long placeId);

    Optional<Review> findByIdAndUserId(Long id, Long userId);

    Page<Review> findByPlaceId(Long placeId, Pageable pageable);

    Page<Review> findByUserId(Long userId, Pageable pageable);

    Page<Review> findByPlaceIdAndUserId(Long placeId, Long userId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.placeId = :placeId")
    Double calculateAverageRatingByPlaceId(@Param("placeId") Long placeId);

    long countByPlaceId(Long placeId);
}
