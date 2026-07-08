package com.tripplanner.backend.repository;

import com.tripplanner.backend.domain.Office;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
    List<Office> findByCityId(Long cityId);
}
