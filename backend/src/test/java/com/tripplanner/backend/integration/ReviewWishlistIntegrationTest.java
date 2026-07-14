package com.tripplanner.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewWishlistIntegrationTest extends IntegrationTestBase {

    @Test
    void userCreatesReviewAndDuplicateReviewReturns409InsteadOf500() throws Exception {
        String adminToken = adminToken();
        String userToken = userToken();
        long cityId = createCity(adminToken, "Review City");
        long cafeTypeId = placeTypeId("CAFE");
        long placeId = createPlace(adminToken, cityId, cafeTypeId, "Review cafe", 0.0, 0.0);

        mockMvc.perform(post("/reviews")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "placeId": %d,
                                  "rating": 5,
                                  "comment": "Great"
                                }
                                """.formatted(placeId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5));

        mockMvc.perform(post("/reviews")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "placeId": %d,
                                  "rating": 4,
                                  "comment": "Again"
                                }
                                """.formatted(placeId))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void userCannotUseAdminReviewFilters() throws Exception {
        mockMvc.perform(get("/reviews")
                        .header("Authorization", bearer(userToken()))
                        .param("cityId", "1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void wishlistAddDuplicateListAndDeleteWorkEndToEnd() throws Exception {
        String adminToken = adminToken();
        String userToken = userToken();
        long cityId = createCity(adminToken, "Wishlist City");
        long cafeTypeId = placeTypeId("CAFE");
        long placeId = createPlace(adminToken, cityId, cafeTypeId, "Wishlist cafe", 0.0, 0.0);

        MvcResult add = mockMvc.perform(post("/wishlists")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "placeId": %d
                                }
                                """.formatted(placeId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.place.name").value("Wishlist cafe"))
                .andReturn();

        long wishlistId = read(add).get("id").asLong();

        mockMvc.perform(post("/wishlists")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "placeId": %d
                                }
                                """.formatted(placeId))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        mockMvc.perform(get("/wishlists")
                        .header("Authorization", bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placeId").value(placeId));

        mockMvc.perform(delete("/wishlists/{id}", wishlistId)
                        .header("Authorization", bearer(userToken)))
                .andExpect(status().isNoContent());
    }
}
