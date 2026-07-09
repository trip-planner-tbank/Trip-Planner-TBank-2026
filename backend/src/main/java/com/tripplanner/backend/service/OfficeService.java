package com.tripplanner.backend.service;

import com.tripplanner.backend.domain.Office;
import com.tripplanner.backend.domain.City;
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
    private final GeocodingService geocodingService;

    @Transactional
    public Office create(OfficeRequest request) {
        City city = findCityOrThrow(request.cityId());
        Coordinates coordinates = resolveCoordinates(request, city);
        Office office = Office.builder()
                .cityId(request.cityId())
                .name(request.name())
                .address(request.address())
                .latitude(coordinates.latitude())
                .longitude(coordinates.longitude())
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
        City city = findCityOrThrow(request.cityId());
        Coordinates coordinates = resolveCoordinates(request, city);
        Office office = findOrThrow(id);
        office.setCityId(request.cityId());
        office.setName(request.name());
        office.setAddress(request.address());
        office.setLatitude(coordinates.latitude());
        office.setLongitude(coordinates.longitude());
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
        findCityOrThrow(cityId);
    }

    private City findCityOrThrow(Long cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new NotFoundException("City not found"));
    }

    private Coordinates resolveCoordinates(OfficeRequest request, City city) {
        if ((request.latitude() == null) != (request.longitude() == null)) {
            throw new com.tripplanner.backend.exception.ValidationException(
                    "latitude and longitude must be provided together");
        }
        if (request.latitude() != null) {
            return new Coordinates(request.latitude(), request.longitude());
        }
        var result = geocodingService.geocode(
                request.address() + ", " + city.getName() + ", " + city.getCountry());
        return new Coordinates(result.latitude(), result.longitude());
    }

    private record Coordinates(double latitude, double longitude) {
    }
}
