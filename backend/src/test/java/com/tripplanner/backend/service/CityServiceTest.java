package com.tripplanner.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tripplanner.backend.domain.City;
import com.tripplanner.backend.dto.city.CityRequest;
import com.tripplanner.backend.exception.ConflictException;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.repository.CityRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock private CityRepository cityRepository;
    @InjectMocks private CityService cityService;

    @Test
    void createsCityWhenNameAndCountryAreUnique() {
        when(cityRepository.existsByNameIgnoreCaseAndCountryIgnoreCase("Dushanbe", "Tajikistan"))
                .thenReturn(false);
        when(cityRepository.save(org.mockito.ArgumentMatchers.any(City.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        City city = cityService.create(new CityRequest("Dushanbe", "Tajikistan"));

        assertThat(city.getName()).isEqualTo("Dushanbe");
        assertThat(city.getCountry()).isEqualTo("Tajikistan");
        ArgumentCaptor<City> captor = ArgumentCaptor.forClass(City.class);
        verify(cityRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Dushanbe");
    }

    @Test
    void rejectsDuplicateCityOnCreate() {
        when(cityRepository.existsByNameIgnoreCaseAndCountryIgnoreCase("Dushanbe", "Tajikistan"))
                .thenReturn(true);

        assertThatThrownBy(() -> cityService.create(new CityRequest("Dushanbe", "Tajikistan")))
                .isInstanceOf(ConflictException.class)
                .hasMessage("City already exists");
    }

    @Test
    void rejectsUpdateWhenAnotherCityHasSameNameAndCountry() {
        City current = City.builder().id(1L).name("Old").country("Tajikistan").build();
        City duplicate = City.builder().id(2L).name("Dushanbe").country("Tajikistan").build();
        when(cityRepository.findById(1L)).thenReturn(Optional.of(current));
        when(cityRepository.findByNameIgnoreCaseAndCountryIgnoreCase("Dushanbe", "Tajikistan"))
                .thenReturn(Optional.of(duplicate));

        assertThatThrownBy(() -> cityService.update(1L, new CityRequest("Dushanbe", "Tajikistan")))
                .isInstanceOf(ConflictException.class)
                .hasMessage("City already exists");
    }

    @Test
    void throwsNotFoundForMissingCity() {
        when(cityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityService.get(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("City not found");
    }
}
