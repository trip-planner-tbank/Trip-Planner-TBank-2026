package com.tripplanner.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends IntegrationTestBase {

    @Test
    void protectedEndpointWithoutTokenReturns401InsteadOf500() throws Exception {
        mockMvc.perform(get("/cities"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidLoginReturns401InsteadOf500() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "username": "user2",
                                  "password": "wrong"
                                }
                                """)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void signupLoginRefreshAndLogoutWorkEndToEnd() throws Exception {
        MvcResult signup = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "username": "new_user",
                                  "email": "new_user@example.com",
                                  "password": "Qwer123!"
                                }
                                """)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        String refreshToken = read(signup).get("refreshToken").asText();

        MvcResult refresh = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("""
                                {
                                  "refreshToken": "%s"
                                }
                                """.formatted(refreshToken))))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = read(refresh).get("accessToken").asText();
        assertThat(accessToken).isNotBlank();

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", bearer(accessToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully logged out"));
    }
}
