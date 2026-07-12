package com.tripplanner.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlaceIntegrationTest extends IntegrationTestBase {

    @Test
    void adminCreatesCityOfficeAndPlacesThenListsByDistance() throws Exception {
        String adminToken = adminToken();
        long cafeTypeId = placeTypeId("CAFE");
        long cityId = createCity(adminToken, "Distance City");
        long officeId = createOffice(adminToken, cityId, "Main office", 0.0, 0.0);
        createPlace(adminToken, cityId, cafeTypeId, "Far cafe", 0.0, 2.0);
        createPlace(adminToken, cityId, cafeTypeId, "Near cafe", 0.0, 1.0);

        mockMvc.perform(get("/places")
                        .header("Authorization", bearer(adminToken))
                        .param("officeId", String.valueOf(officeId))
                        .param("placeTypeId", String.valueOf(cafeTypeId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", contains("Near cafe", "Far cafe")))
                .andExpect(jsonPath("$[*].distanceKm", everyItem(notNullValue())));
    }

    @Test
    void regularUserCanCreateNonHotelButCannotCreateHotel() throws Exception {
        String adminToken = adminToken();
        String userToken = userToken();
        long cafeTypeId = placeTypeId("CAFE");
        long hotelTypeId = placeTypeId("HOTEL");
        long cityId = createCity(adminToken, "Rules City");

        mockMvc.perform(post("/places")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "cityId": %d,
                                  "placeTypeId": %d,
                                  "name": "User cafe",
                                  "address": "Cafe address",
                                  "latitude": 1.000000,
                                  "longitude": 1.000000
                                }
                                """.formatted(cityId, cafeTypeId))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/places")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "cityId": %d,
                                  "placeTypeId": %d,
                                  "name": "User hotel",
                                  "address": "Hotel address",
                                  "latitude": 1.000000,
                                  "longitude": 1.000000
                                }
                                """.formatted(cityId, hotelTypeId))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void validationErrorsReturn400InsteadOf500() throws Exception {
        mockMvc.perform(get("/places")
                        .header("Authorization", bearer(adminToken()))
                        .param("maxDistanceKm", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
