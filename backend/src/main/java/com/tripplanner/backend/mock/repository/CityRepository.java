package com.tripplanner.backend.mock.repository;

import com.tripplanner.backend.mock.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
}
