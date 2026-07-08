package com.tripplanner.backend.repository;

import com.tripplanner.backend.domain.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserIdOrderByAddedAtDesc(Long userId);

    boolean existsByUserIdAndPlaceId(Long userId, Long placeId);

    Optional<Wishlist> findByIdAndUserId(Long id, Long userId);
}
