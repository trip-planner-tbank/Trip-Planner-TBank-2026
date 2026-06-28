package com.tripplanner.backend.mock.repository;

import com.tripplanner.backend.mock.domain.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaceTypeRepository extends JpaRepository<PlaceType, Long> {
    Optional<PlaceType> findByCode(String code);
}
