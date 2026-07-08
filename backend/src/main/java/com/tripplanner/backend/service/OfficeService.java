package com.tripplanner.backend.service;

import com.tripplanner.backend.domain.Office;
import com.tripplanner.backend.dto.office.OfficeRequest;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.repository.CityRepository;
import com.tripplanner.backend.repository.OfficeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OfficeService {

    private final OfficeRepository officeRepository;
    private final CityRepository cityRepository;

    @Transactional
    public Office create(OfficeRequest request) {
        ensureCityExists(request.cityId());
        Office office = Office.builder()
                .cityId(request.cityId())
                .name(request.name())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();
        return officeRepository.save(office);
    }

    @Transactional(readOnly = true)
    public List<Office> list(Long cityId) {
        if (cityId == null) {
            return officeRepository.findAll();
        }
        ensureCityExists(cityId);
        return officeRepository.findByCityId(cityId);
    }

    @Transactional(readOnly = true)
    public Office get(Long id) {
        return findOrThrow(id);
    }

    @Transactional
    public Office update(Long id, OfficeRequest request) {
        ensureCityExists(request.cityId());
        Office office = findOrThrow(id);
        office.setCityId(request.cityId());
        office.setName(request.name());
        office.setAddress(request.address());
        office.setLatitude(request.latitude());
        office.setLongitude(request.longitude());
        return officeRepository.save(office);
    }

    @Transactional
    public void delete(Long id) {
        Office office = findOrThrow(id);
        officeRepository.delete(office);
    }

    private Office findOrThrow(Long id) {
        return officeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Office not found"));
    }

    private void ensureCityExists(Long cityId) {
        if (!cityRepository.existsById(cityId)) {
            throw new NotFoundException("City not found");
        }
    }
}
