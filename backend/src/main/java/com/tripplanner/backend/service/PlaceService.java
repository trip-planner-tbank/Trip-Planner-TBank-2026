package com.tripplanner.backend.service;

import com.tripplanner.backend.controller.PlaceController.CreatePlaceRequest;
import com.tripplanner.backend.domain.Office;
import com.tripplanner.backend.domain.City;
import com.tripplanner.backend.domain.Place;
import com.tripplanner.backend.domain.PlaceType;
import com.tripplanner.backend.dto.place.PlaceWithDistanceResponse;
import com.tripplanner.backend.exception.ForbiddenException;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.exception.ValidationException;
import com.tripplanner.backend.repository.CityRepository;
import com.tripplanner.backend.repository.OfficeRepository;
import com.tripplanner.backend.repository.PlaceRepository;
import com.tripplanner.backend.repository.PlaceTypeRepository;
import com.tripplanner.backend.security.SecurityUtil;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private static final double EARTH_RADIUS_KM = 6371.0088;

    private final PlaceRepository placeRepository;
    private final CityRepository cityRepository;
    private final PlaceTypeRepository placeTypeRepository;
    private final OfficeRepository officeRepository;
    private final GeocodingService geocodingService;

    @Transactional(readOnly = true)
    public List<PlaceWithDistanceResponse> list(
            Long cityId,
            Long placeTypeId,
            Long officeId,
            Long referencePlaceId,
            Double maxDistanceKm,
            int page,
            int size) {
        validatePaging(page, size);
        if (officeId != null && referencePlaceId != null) {
            throw new ValidationException("Provide either officeId or referencePlaceId, not both");
        }
        if (maxDistanceKm != null && officeId == null && referencePlaceId == null) {
            throw new ValidationException("maxDistanceKm requires officeId or referencePlaceId");
        }
        if (cityId != null && !cityRepository.existsById(cityId)) {
            throw new NotFoundException("City not found");
        }
        if (placeTypeId != null && !placeTypeRepository.existsById(placeTypeId)) {
            throw new NotFoundException("Place type not found");
        }

        Coordinates reference = null;
        Long excludedPlaceId = null;
        if (officeId != null) {
            Office office = officeRepository.findById(officeId)
                    .orElseThrow(() -> new NotFoundException("Office not found"));
            reference = new Coordinates(office.getLatitude(), office.getLongitude());
        } else if (referencePlaceId != null) {
            Place place = findActivePlaceOrThrow(referencePlaceId);
            reference = new Coordinates(place.getLatitude(), place.getLongitude());
            excludedPlaceId = place.getId();
        }

        final Coordinates distanceReference = reference;
        final Long placeToExclude = excludedPlaceId;
        List<PlaceWithDistanceResponse> matches = placeRepository.findAll().stream()
                .filter(place -> Boolean.TRUE.equals(place.getIsActive()))
                .filter(place -> cityId == null || cityId.equals(place.getCityId()))
                .filter(place -> placeTypeId == null || placeTypeId.equals(place.getPlaceTypeId()))
                .filter(place -> placeToExclude == null || !placeToExclude.equals(place.getId()))
                .map(place -> PlaceWithDistanceResponse.from(
                        place,
                        distanceReference == null ? null : distanceKm(
                                distanceReference.latitude(),
                                distanceReference.longitude(),
                                place.getLatitude(),
                                place.getLongitude())))
                .filter(place -> maxDistanceKm == null || place.distanceKm() <= maxDistanceKm)
                .sorted(distanceReference == null
                        ? Comparator.comparing(PlaceWithDistanceResponse::id)
                        : Comparator.comparing(PlaceWithDistanceResponse::distanceKm)
                                .thenComparing(PlaceWithDistanceResponse::id))
                .toList();

        int from = (int) Math.min((long) page * size, matches.size());
        int to = Math.min(from + size, matches.size());
        return matches.subList(from, to);
    }

    @Transactional(readOnly = true)
    public List<PlaceWithDistanceResponse> nearbyOffice(Long officeId, Long placeTypeId, Double maxDistanceKm) {
        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new NotFoundException("Office not found"));
        return collectNearby(office.getCityId(), placeTypeId, officeId, null, maxDistanceKm);
    }

    @Transactional(readOnly = true)
    public List<PlaceWithDistanceResponse> nearbyPlace(Long placeId, Long placeTypeId, Double maxDistanceKm) {
        Place place = findActivePlaceOrThrow(placeId);
        return collectNearby(place.getCityId(), placeTypeId, null, placeId, maxDistanceKm);
    }

    @Transactional(readOnly = true)
    public Place get(Long id) {
        return findActivePlaceOrThrow(id);
    }

    @Transactional
    public Place create(CreatePlaceRequest request) {
        References references = validateReferences(request.cityId(), request.placeTypeId());
        PlaceType placeType = references.placeType();
        if ("HOTEL".equalsIgnoreCase(placeType.getCode()) && !SecurityUtil.isAdmin()) {
            throw new ForbiddenException("Only admins can create hotel places");
        }
        Coordinates coordinates = resolveCoordinates(request, references.city());
        return placeRepository.save(Place.builder()
                .cityId(request.cityId())
                .placeTypeId(request.placeTypeId())
                .createdBy(SecurityUtil.getCurrentUserId())
                .name(request.name())
                .address(request.address())
                .latitude(coordinates.latitude())
                .longitude(coordinates.longitude())
                .description(request.description())
                .isActive(true)
                .avgRating(0.0)
                .build());
    }

    @Transactional
    public Place update(Long id, CreatePlaceRequest request) {
        References references = validateReferences(request.cityId(), request.placeTypeId());
        Coordinates coordinates = resolveCoordinates(request, references.city());
        Place place = findActivePlaceOrThrow(id);
        place.setCityId(request.cityId());
        place.setPlaceTypeId(request.placeTypeId());
        place.setName(request.name());
        place.setAddress(request.address());
        place.setLatitude(coordinates.latitude());
        place.setLongitude(coordinates.longitude());
        place.setDescription(request.description());
        return placeRepository.save(place);
    }

    @Transactional
    public void delete(Long id) {
        Place place = findActivePlaceOrThrow(id);
        place.setIsActive(false);
        placeRepository.save(place);
    }

    private Place findActivePlaceOrThrow(Long id) {
        return placeRepository.findById(id)
                .filter(place -> Boolean.TRUE.equals(place.getIsActive()))
                .orElseThrow(() -> new NotFoundException("Place not found"));
    }

    private References validateReferences(Long cityId, Long placeTypeId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new NotFoundException("City not found"));
        PlaceType placeType = placeTypeRepository.findById(placeTypeId)
                .orElseThrow(() -> new NotFoundException("Place type not found"));
        return new References(city, placeType);
    }

    private Coordinates resolveCoordinates(CreatePlaceRequest request, City city) {
        if ((request.latitude() == null) != (request.longitude() == null)) {
            throw new ValidationException("latitude and longitude must be provided together");
        }
        if (request.latitude() != null) {
            return new Coordinates(request.latitude(), request.longitude());
        }
        var result = geocodingService.geocode(
                request.address() + ", " + city.getName() + ", " + city.getCountry());
        return new Coordinates(result.latitude(), result.longitude());
    }

    private void validatePaging(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ValidationException("page must be >= 0 and size must be between 1 and 100");
        }
    }

    private List<PlaceWithDistanceResponse> collectNearby(
            Long cityId, Long placeTypeId, Long officeId, Long referencePlaceId, Double maxDistanceKm) {
        List<PlaceWithDistanceResponse> result = new ArrayList<>();
        for (int page = 0; ; page++) {
            List<PlaceWithDistanceResponse> batch =
                    list(cityId, placeTypeId, officeId, referencePlaceId, maxDistanceKm, page, 100);
            result.addAll(batch);
            if (batch.size() < 100) {
                return result;
            }
        }
    }

    static double distanceKm(double latitude1, double longitude1, double latitude2, double longitude2) {
        double latitudeDelta = Math.toRadians(latitude2 - latitude1);
        double longitudeDelta = Math.toRadians(longitude2 - longitude1);
        double a = Math.sin(latitudeDelta / 2) * Math.sin(latitudeDelta / 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                * Math.sin(longitudeDelta / 2) * Math.sin(longitudeDelta / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private record Coordinates(double latitude, double longitude) {
    }

    private record References(City city, PlaceType placeType) {
    }
}
