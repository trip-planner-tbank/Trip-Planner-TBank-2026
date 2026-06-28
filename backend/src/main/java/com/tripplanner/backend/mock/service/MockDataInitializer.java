package com.tripplanner.backend.mock.service;

import com.tripplanner.backend.mock.domain.*;
import com.tripplanner.backend.mock.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MockDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PlaceTypeRepository placeTypeRepository;
    private final CityRepository cityRepository;
    private final PlaceRepository placeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Mock data already initialized, skipping.");
            return;
        }

        User admin = userRepository.save(User.builder()
                .username("admin")
                .email("admin@tripplanner.local")
                .password(passwordEncoder.encode("Admin123!"))
                .role(Role.ADMIN)
                .build());

        User user = userRepository.save(User.builder()
                .username("user")
                .email("user@tripplanner.local")
                .password(passwordEncoder.encode("User123!"))
                .role(Role.USER)
                .build());

        log.info("Created mock users: admin(id={}), user(id={})", admin.getId(), user.getId());

        PlaceType hotelType = placeTypeRepository.save(PlaceType.builder()
                .name("Hotel")
                .code("HOTEL")
                .build());

        PlaceType cafeType = placeTypeRepository.save(PlaceType.builder()
                .name("Cafe")
                .code("CAFE")
                .build());

        log.info("Created mock place types: HOTEL(id={}), CAFE(id={})", hotelType.getId(), cafeType.getId());

        City moscow = cityRepository.save(City.builder()
                .name("Moscow")
                .country("Russia")
                .build());

        log.info("Created mock city: Moscow(id={})", moscow.getId());

        Place grandHotel = placeRepository.save(Place.builder()
                .cityId(moscow.getId())
                .placeTypeId(hotelType.getId())
                .createdBy(admin.getId())
                .name("Grand Hotel")
                .address("Arbat 10")
                .latitude(55.7512)
                .longitude(37.6184)
                .description("Luxury hotel in city center")
                .isActive(true)
                .avgRating(0.0)
                .build());

        Place coffeeHouse = placeRepository.save(Place.builder()
                .cityId(moscow.getId())
                .placeTypeId(cafeType.getId())
                .createdBy(user.getId())
                .name("Coffee House")
                .address("Tverskaya 15")
                .latitude(55.755826)
                .longitude(37.617300)
                .description("Popular cafe near the office")
                .isActive(true)
                .avgRating(0.0)
                .build());

        log.info("Created mock places: Grand Hotel(id={}), Coffee House(id={})",
                grandHotel.getId(), coffeeHouse.getId());
    }
}
