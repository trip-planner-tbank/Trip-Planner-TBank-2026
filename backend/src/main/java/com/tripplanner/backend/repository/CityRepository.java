package com.tripplanner.backend.repository;

import com.tripplanner.backend.domain.City;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    boolean existsByNameIgnoreCaseAndCountryIgnoreCase(String name, String country);

    Optional<City> findByNameIgnoreCaseAndCountryIgnoreCase(String name, String country);
}
