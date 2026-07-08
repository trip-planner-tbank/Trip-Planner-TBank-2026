package com.tripplanner.backend.service;

import com.tripplanner.backend.domain.HotelDetails;
import com.tripplanner.backend.domain.Place;
import com.tripplanner.backend.domain.PlaceType;
import com.tripplanner.backend.dto.hotel.CreateHotelDetailsRequest;
import com.tripplanner.backend.dto.hotel.HotelDetailsResponse;
import com.tripplanner.backend.exception.ConflictException;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.exception.ValidationException;
import com.tripplanner.backend.repository.HotelDetailsRepository;
import com.tripplanner.backend.repository.PlaceRepository;
import com.tripplanner.backend.repository.PlaceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HotelDetailsService {

    private final HotelDetailsRepository hotelDetailsRepository;
    private final PlaceRepository placeRepository;
    private final PlaceTypeRepository placeTypeRepository;

    @Transactional(readOnly = true)
    public HotelDetailsResponse getByPlaceId(Long placeId) {
        Place place = findPlaceOrThrow(placeId);
        assertIsHotel(place);

        HotelDetails details = hotelDetailsRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new NotFoundException("Hotel details not found"));
        return mapToResponse(details);
    }

    @Transactional
    public HotelDetailsResponse create(Long placeId, CreateHotelDetailsRequest request) {
        Place place = findPlaceOrThrow(placeId);
        assertIsHotel(place);

        if (hotelDetailsRepository.existsByPlaceId(placeId)) {
            throw new ConflictException("Hotel details already exist for this place");
        }

        HotelDetails details = HotelDetails.builder()
                .placeId(placeId)
                .starRating(request.getStarRating())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .roomCount(request.getRoomCount())
                .build();

        return mapToResponse(hotelDetailsRepository.save(details));
    }

    @Transactional
    public HotelDetailsResponse update(Long placeId, CreateHotelDetailsRequest request) {
        Place place = findPlaceOrThrow(placeId);
        assertIsHotel(place);

        HotelDetails details = hotelDetailsRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new NotFoundException("Hotel details not found"));

        details.setStarRating(request.getStarRating());
        details.setPhone(request.getPhone());
        details.setWebsite(request.getWebsite());
        details.setRoomCount(request.getRoomCount());

        return mapToResponse(hotelDetailsRepository.save(details));
    }

    private Place findPlaceOrThrow(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("Place not found"));
    }

    private void assertIsHotel(Place place) {
        PlaceType placeType = placeTypeRepository.findById(place.getPlaceTypeId())
                .orElseThrow(() -> new NotFoundException("Place type not found"));
        if (!"HOTEL".equalsIgnoreCase(placeType.getCode())) {
            throw new ValidationException("Place is not a hotel");
        }
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
}
