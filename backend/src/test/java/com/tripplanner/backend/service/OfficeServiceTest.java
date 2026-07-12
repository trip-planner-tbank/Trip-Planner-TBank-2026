package com.tripplanner.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tripplanner.backend.domain.City;
import com.tripplanner.backend.domain.Office;
import com.tripplanner.backend.dto.geocoding.GeocodingResponse;
import com.tripplanner.backend.dto.office.OfficeRequest;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.exception.ValidationException;
import com.tripplanner.backend.repository.CityRepository;
import com.tripplanner.backend.repository.OfficeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OfficeServiceTest {

    @Mock private OfficeRepository officeRepository;
    @Mock private CityRepository cityRepository;
    @Mock private GeocodingService geocodingService;
    @InjectMocks private OfficeService officeService;

    @Test
    void createsOfficeWithProvidedCoordinatesWithoutGeocoding() {
        City city = City.builder().id(1L).name("Dushanbe").country("Tajikistan").build();
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(officeRepository.save(org.mockito.ArgumentMatchers.any(Office.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Office office = officeService.create(new OfficeRequest(
                1L, "Main office", "Rudaki 10", 38.5737, 68.7738));

        assertThat(office.getLatitude()).isEqualTo(38.5737);
        assertThat(office.getLongitude()).isEqualTo(68.7738);
        verify(geocodingService, never()).geocode(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void geocodesOfficeWhenCoordinatesAreMissing() {
        City city = City.builder().id(1L).name("Dushanbe").country("Tajikistan").build();
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(geocodingService.geocode("Rudaki 10, Dushanbe, Tajikistan"))
                .thenReturn(new GeocodingResponse("Rudaki 10", 38.5737, 68.7738));
        when(officeRepository.save(org.mockito.ArgumentMatchers.any(Office.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Office office = officeService.create(new OfficeRequest(
                1L, "Main office", "Rudaki 10", null, null));

        assertThat(office.getLatitude()).isEqualTo(38.5737);
        assertThat(office.getLongitude()).isEqualTo(68.7738);
    }

    @Test
    void rejectsPartialCoordinates() {
        City city = City.builder().id(1L).name("Dushanbe").country("Tajikistan").build();
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));

        assertThatThrownBy(() -> officeService.create(new OfficeRequest(
                1L, "Main office", "Rudaki 10", 38.5737, null)))
                .isInstanceOf(ValidationException.class)
                .hasMessage("latitude and longitude must be provided together");
    }

    @Test
    void listByCityRejectsMissingCity() {
        when(cityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> officeService.list(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("City not found");
    }

    @Test
    void listByCityReturnsOnlyRepositoryResultForExistingCity() {
        City city = City.builder().id(1L).name("Dushanbe").country("Tajikistan").build();
        Office office = Office.builder().id(10L).cityId(1L).name("Main").address("Rudaki").build();
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        when(officeRepository.findByCityId(1L)).thenReturn(List.of(office));

        assertThat(officeService.list(1L)).containsExactly(office);
    }
}
