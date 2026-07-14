package com.tripplanner.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tripplanner.backend.domain.HotelDetails;
import com.tripplanner.backend.domain.Place;
import com.tripplanner.backend.domain.PlaceType;
import com.tripplanner.backend.dto.hotel.CreateHotelDetailsRequest;
import com.tripplanner.backend.exception.ConflictException;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.exception.ValidationException;
import com.tripplanner.backend.repository.HotelDetailsRepository;
import com.tripplanner.backend.repository.PlaceRepository;
import com.tripplanner.backend.repository.PlaceTypeRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HotelDetailsServiceTest {

    @Mock private HotelDetailsRepository hotelDetailsRepository;
    @Mock private PlaceRepository placeRepository;
    @Mock private PlaceTypeRepository placeTypeRepository;
    @InjectMocks private HotelDetailsService hotelDetailsService;

    @Test
    void createsHotelDetailsForHotelPlace() {
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place(1L, 1L)));
        when(placeTypeRepository.findById(1L))
                .thenReturn(Optional.of(PlaceType.builder().id(1L).code("HOTEL").name("Hotel").build()));
        when(hotelDetailsRepository.existsByPlaceId(1L)).thenReturn(false);
        when(hotelDetailsRepository.save(org.mockito.ArgumentMatchers.any(HotelDetails.class)))
                .thenAnswer(invocation -> {
                    HotelDetails details = invocation.getArgument(0);
                    details.setId(10L);
                    return details;
                });

        var response = hotelDetailsService.create(1L,
                new CreateHotelDetailsRequest(5, "+992", "https://hotel.example", 100));

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getPlaceId()).isEqualTo(1L);
        assertThat(response.getStarRating()).isEqualTo(5);
    }

    @Test
    void rejectsDetailsForNonHotelPlace() {
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place(1L, 2L)));
        when(placeTypeRepository.findById(2L))
                .thenReturn(Optional.of(PlaceType.builder().id(2L).code("CAFE").name("Cafe").build()));

        assertThatThrownBy(() -> hotelDetailsService.create(1L,
                new CreateHotelDetailsRequest(5, null, null, 10)))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Place is not a hotel");
    }

    @Test
    void rejectsDuplicateHotelDetails() {
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place(1L, 1L)));
        when(placeTypeRepository.findById(1L))
                .thenReturn(Optional.of(PlaceType.builder().id(1L).code("HOTEL").name("Hotel").build()));
        when(hotelDetailsRepository.existsByPlaceId(1L)).thenReturn(true);

        assertThatThrownBy(() -> hotelDetailsService.create(1L,
                new CreateHotelDetailsRequest(5, null, null, 10)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Hotel details already exist for this place");
    }

    @Test
    void getByPlaceIdRejectsMissingDetails() {
        when(placeRepository.findById(1L)).thenReturn(Optional.of(place(1L, 1L)));
        when(placeTypeRepository.findById(1L))
                .thenReturn(Optional.of(PlaceType.builder().id(1L).code("HOTEL").name("Hotel").build()));
        when(hotelDetailsRepository.findByPlaceId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelDetailsService.getByPlaceId(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Hotel details not found");
    }

    private static Place place(Long id, Long placeTypeId) {
        return Place.builder()
                .id(id)
                .cityId(1L)
                .placeTypeId(placeTypeId)
                .createdBy(1L)
                .name("Place")
                .address("Address")
                .latitude(0.0)
                .longitude(0.0)
                .isActive(true)
                .avgRating(0.0)
                .build();
    }
}
