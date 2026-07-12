package com.tripplanner.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tripplanner.backend.controller.PlaceController.CreatePlaceRequest;
import com.tripplanner.backend.domain.Place;
import com.tripplanner.backend.domain.PlaceType;
import com.tripplanner.backend.domain.City;
import com.tripplanner.backend.exception.ForbiddenException;
import com.tripplanner.backend.repository.CityRepository;
import com.tripplanner.backend.repository.OfficeRepository;
import com.tripplanner.backend.repository.PlaceRepository;
import com.tripplanner.backend.repository.PlaceTypeRepository;
import com.tripplanner.backend.user.AppUser;
import com.tripplanner.backend.user.Role;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock private PlaceRepository placeRepository;
    @Mock private CityRepository cityRepository;
    @Mock private PlaceTypeRepository placeTypeRepository;
    @Mock private OfficeRepository officeRepository;
    @Mock private GeocodingService geocodingService;
    @InjectMocks private PlaceService placeService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void calculatesHaversineDistance() {
        assertThat(PlaceService.distanceKm(0, 0, 0, 1))
                .isCloseTo(111.195, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    void filtersInactivePlacesAndSortsByDistance() {
        Place reference = place(1L, true, 0.0, 0.0);
        Place farther = place(2L, true, 0.0, 2.0);
        Place nearer = place(3L, true, 0.0, 1.0);
        Place inactive = place(4L, false, 0.0, 0.5);
        when(placeRepository.findById(1L)).thenReturn(Optional.of(reference));
        when(placeRepository.findAll()).thenReturn(List.of(reference, farther, nearer, inactive));

        var result = placeService.list(null, null, null, 1L, null, 0, 20);

        assertThat(result).extracting(item -> item.id()).containsExactly(3L, 2L);
        assertThat(result).extracting(item -> item.distanceKm()).isSorted();
    }

    @Test
    void ordinaryUserCannotCreateHotel() {
        AppUser user = new AppUser("user2", "user2@example.com", "hash", Role.USER);
        user.setId(2L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        when(cityRepository.findById(1L))
                .thenReturn(Optional.of(City.builder().id(1L).name("Dushanbe").country("Tajikistan").build()));
        when(placeTypeRepository.findById(1L))
                .thenReturn(Optional.of(PlaceType.builder().id(1L).code("HOTEL").name("Hotel").build()));

        CreatePlaceRequest request = new CreatePlaceRequest(
                1L, 1L, "Hotel", "Address", 1.0, 1.0, null);

        assertThatThrownBy(() -> placeService.create(request))
                .isInstanceOf(ForbiddenException.class);
    }

    private static Place place(Long id, boolean active, double latitude, double longitude) {
        return Place.builder()
                .id(id)
                .cityId(1L)
                .placeTypeId(2L)
                .createdBy(1L)
                .name("Place " + id)
                .address("Address")
                .latitude(latitude)
                .longitude(longitude)
                .isActive(active)
                .avgRating(0.0)
                .build();
    }
}
