package com.tripplanner.backend.repository;

import com.tripplanner.backend.domain.HotelDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelDetailsRepository extends JpaRepository<HotelDetails, Long> {
    Optional<HotelDetails> findByPlaceId(Long placeId);

    boolean existsByPlaceId(Long placeId);
}
