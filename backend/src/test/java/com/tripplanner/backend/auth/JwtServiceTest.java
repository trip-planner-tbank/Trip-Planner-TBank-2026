package com.tripplanner.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripplanner.backend.user.AppUser;
import com.tripplanner.backend.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    private JwtService jwtService;
    private AppUser user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(new ObjectMapper());
        ReflectionTestUtils.setField(jwtService, "secret", "test-secret-that-is-long-enough-for-hmac");
        ReflectionTestUtils.setField(jwtService, "accessTokenSeconds", 900L);
        ReflectionTestUtils.setField(jwtService, "refreshTokenSeconds", 604800L);

        user = new AppUser("user2", "user2@example.com", "hash", Role.USER);
        user.setId(2L);
    }

    @Test
    void generatesValidAccessTokenWithExpectedClaims() {
        String token = jwtService.generateAccessToken(user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("user2");
        assertThat(jwtService.extractTokenType(token)).isEqualTo("access");
        assertThat(jwtService.extractExpiration(token)).isNotNull();
        assertThat(jwtService.isAccessTokenValid(token, user)).isTrue();
    }

    @Test
    void rejectsRefreshTokenAsAccessToken() {
        String refreshToken = jwtService.generateRefreshToken(user);

        assertThat(jwtService.extractTokenType(refreshToken)).isEqualTo("refresh");
        assertThat(jwtService.isAccessTokenValid(refreshToken, user)).isFalse();
        assertThat(jwtService.isRefreshTokenValid(refreshToken, user)).isTrue();
    }

    @Test
    void rejectsTokenForDifferentUser() {
        String token = jwtService.generateAccessToken(user);
        AppUser anotherUser = new AppUser("user3", "user3@example.com", "hash", Role.USER);

        assertThat(jwtService.isAccessTokenValid(token, anotherUser)).isFalse();
    }

    @Test
    void generatedTokensAreUniqueEvenForSameUserAndType() {
        String first = jwtService.generateRefreshToken(user);
        String second = jwtService.generateRefreshToken(user);

        assertThat(second).isNotEqualTo(first);
    }

    @Test
    void tamperedTokenCannotBeParsedOrValidated() {
        String token = jwtService.generateAccessToken(user);
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtService.isAccessTokenValid(tampered, user)).isFalse();
        assertThatThrownBy(() -> jwtService.extractUsername(tampered))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid JWT");
    }
}
