package com.tripplanner.backend.service;

import com.tripplanner.backend.domain.City;
import com.tripplanner.backend.dto.city.CityRequest;
import com.tripplanner.backend.exception.ConflictException;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.repository.CityRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    @Transactional
    public City create(CityRequest request) {
        if (cityRepository.existsByNameIgnoreCaseAndCountryIgnoreCase(request.name(), request.country())) {
            throw new ConflictException("City already exists");
        }

        City city = City.builder()
                .name(request.name())
                .country(request.country())
                .build();
        return cityRepository.save(city);
    }

    @Transactional(readOnly = true)
    public List<City> list() {
        return cityRepository.findAll();
    }

    @Transactional(readOnly = true)
    public City get(Long id) {
        return findOrThrow(id);
    }

    @Transactional
    public City update(Long id, CityRequest request) {
        City city = findOrThrow(id);
        cityRepository.findByNameIgnoreCaseAndCountryIgnoreCase(request.name(), request.country())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("City already exists");
                });

        city.setName(request.name());
        city.setCountry(request.country());
        return cityRepository.save(city);
    }

    @Transactional
    public void delete(Long id) {
        City city = findOrThrow(id);
        cityRepository.delete(city);
    }

    private City findOrThrow(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("City not found"));
    }
}
