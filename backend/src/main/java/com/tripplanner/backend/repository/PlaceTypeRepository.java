package com.tripplanner.backend.repository;

import com.tripplanner.backend.domain.PlaceType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceTypeRepository extends JpaRepository<PlaceType, Long> {
    Optional<PlaceType> findByCode(String code);
}
