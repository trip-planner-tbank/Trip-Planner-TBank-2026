package com.tripplanner.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
abstract class IntegrationTestBase {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("trip_planner_test")
            .withUsername("test")
            .withPassword("test");

    static {
        POSTGRES.start();
    }

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("app.jwt.secret", () -> "integration-test-secret-that-is-long-enough-for-hmac");
        registry.add("app.admin.username", () -> "user1");
        registry.add("app.admin.email", () -> "user1@example.com");
        registry.add("app.admin.password", () -> "Qwer123!");
        registry.add("app.geocoding.base-url", () -> "https://nominatim.invalid");
    }

    @BeforeEach
    void cleanBusinessData() {
        jdbcTemplate.update("delete from refresh_tokens");
        jdbcTemplate.update("delete from wishlists");
        jdbcTemplate.update("delete from reviews");
        jdbcTemplate.update("delete from hotel_details");
        jdbcTemplate.update("delete from places");
        jdbcTemplate.update("delete from offices");
        jdbcTemplate.update("delete from cities");
    }

    protected String adminToken() throws Exception {
        return login("user1", "Qwer123!");
    }

    protected String userToken() throws Exception {
        return login("user2", "Qwer123!");
    }

    protected String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password))))
                .andExpect(status().isOk())
                .andReturn();
        return read(result).get("accessToken").asText();
    }

    protected long placeTypeId(String code) throws Exception {
        MvcResult result = mockMvc.perform(get("/place-types")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andReturn();
        for (JsonNode placeType : read(result)) {
            if (code.equals(placeType.get("code").asText())) {
                return placeType.get("id").asLong();
            }
        }
        throw new AssertionError("Place type not found: " + code);
    }

    protected long createCity(String token, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/cities")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "name": "%s",
                                  "country": "Tajikistan"
                                }
                                """.formatted(name))))
                .andExpect(status().isCreated())
                .andReturn();
        return read(result).get("id").asLong();
    }

    protected long createOffice(String token, long cityId, String name, double latitude, double longitude) throws Exception {
        MvcResult result = mockMvc.perform(post("/offices")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "cityId": %d,
                                  "name": "%s",
                                  "address": "Office address",
                                  "latitude": %.6f,
                                  "longitude": %.6f
                                }
                                """.formatted(cityId, name, latitude, longitude))))
                .andExpect(status().isCreated())
                .andReturn();
        return read(result).get("id").asLong();
    }

    protected long createPlace(
            String token,
            long cityId,
            long placeTypeId,
            String name,
            double latitude,
            double longitude) throws Exception {
        MvcResult result = mockMvc.perform(post("/places")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "cityId": %d,
                                  "placeTypeId": %d,
                                  "name": "%s",
                                  "address": "Place address",
                                  "latitude": %.6f,
                                  "longitude": %.6f,
                                  "description": "Integration test place"
                                }
                                """.formatted(cityId, placeTypeId, name, latitude, longitude))))
                .andExpect(status().isCreated())
                .andReturn();
        return read(result).get("id").asLong();
    }

    protected JsonNode read(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    protected String json(String value) {
        return value.stripIndent();
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }
}
