package com.tripplanner.backend.service;

import com.tripplanner.backend.domain.City;
import com.tripplanner.backend.domain.HotelDetails;
import com.tripplanner.backend.domain.Place;
import com.tripplanner.backend.domain.PlaceType;
import com.tripplanner.backend.dto.hotel.CreateHotelRequest;
import com.tripplanner.backend.dto.hotel.HotelDetailsResponse;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.exception.ValidationException;
import com.tripplanner.backend.repository.CityRepository;
import com.tripplanner.backend.repository.HotelDetailsRepository;
import com.tripplanner.backend.repository.PlaceRepository;
import com.tripplanner.backend.repository.PlaceTypeRepository;
import com.tripplanner.backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final PlaceRepository placeRepository;
    private final HotelDetailsRepository hotelDetailsRepository;
    private final CityRepository cityRepository;
    private final PlaceTypeRepository placeTypeRepository;
    private final GeocodingService geocodingService;

    @Transactional
    public HotelDetailsResponse create(CreateHotelRequest request) {
        City city = cityRepository.findById(request.cityId())
                .orElseThrow(() -> new NotFoundException("City not found"));
        PlaceType hotelType = placeTypeRepository.findByCode("HOTEL")
                .orElseThrow(() -> new NotFoundException("HOTEL place type is not configured"));

        Coordinates coordinates = resolveCoordinates(request, city);

        Place place = Place.builder()
                .cityId(request.cityId())
                .placeTypeId(hotelType.getId())
                .createdBy(SecurityUtil.getCurrentUserId())
                .name(request.name())
                .address(request.address())
                .latitude(coordinates.latitude())
                .longitude(coordinates.longitude())
                .description(request.description())
                .isActive(true)
                .avgRating(0.0)
                .build();

        Place savedPlace = placeRepository.save(place);

        HotelDetails details = HotelDetails.builder()
                .placeId(savedPlace.getId())
                .starRating(request.details().getStarRating())
                .phone(request.details().getPhone())
                .website(request.details().getWebsite())
                .roomCount(request.details().getRoomCount())
                .build();

        HotelDetails savedDetails = hotelDetailsRepository.save(details);
        return mapToResponse(savedDetails);
    }

    private Coordinates resolveCoordinates(CreateHotelRequest request, City city) {
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

    private HotelDetailsResponse mapToResponse(HotelDetails details) {
        return HotelDetailsResponse.builder()
                .id(details.getId())
                .placeId(details.getPlaceId())
                .starRating(details.getStarRating())
                .phone(details.getPhone())
                .website(details.getWebsite())
                .roomCount(details.getRoomCount())
                .build();
    }

    private record Coordinates(double latitude, double longitude) {
    }
}
