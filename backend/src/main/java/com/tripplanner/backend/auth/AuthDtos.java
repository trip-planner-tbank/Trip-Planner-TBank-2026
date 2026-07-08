package com.tripplanner.backend.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record SignupRequest(
            @NotBlank
            @Size(min = 3, max = 50)
            @Pattern(regexp = "^[a-zA-Z0-9_]+$")
            String username,

            @NotBlank
            @Email
            @Size(max = 255)
            String email,

            @NotBlank
            @Size(min = 8, max = 64)
            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$")
            String password
    ) {
    }

    public record LoginRequest(
            @JsonAlias("email")
            @NotBlank
            @Size(min = 3, max = 50)
            String username,

            @NotBlank
            String password
    ) {
    }

    public record RefreshTokenRequest(
            @NotBlank
            String refreshToken
    ) {
    }

    public record TokenResponse(
            String accessToken,
            String refreshToken
    ) {
    }

    public record MessageResponse(
            String message
    ) {
    }
}
