package com.tripplanner.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripplanner.backend.auth.AuthDtos.LoginRequest;
import com.tripplanner.backend.auth.AuthDtos.RefreshTokenRequest;
import com.tripplanner.backend.auth.AuthDtos.SignupRequest;
import com.tripplanner.backend.user.AppUser;
import com.tripplanner.backend.user.AppUserRepository;
import com.tripplanner.backend.user.Role;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

class AuthServiceTest {

    @Mock private AppUserRepository appUserRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;

    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService(new ObjectMapper());
        ReflectionTestUtils.setField(jwtService, "secret", "test-secret-that-is-long-enough-for-hmac");
        ReflectionTestUtils.setField(jwtService, "accessTokenSeconds", 900L);
        ReflectionTestUtils.setField(jwtService, "refreshTokenSeconds", 604800L);
        authService = new AuthService(
                appUserRepository,
                refreshTokenRepository,
                passwordEncoder,
                authenticationManager,
                userDetailsService,
                jwtService);
    }

    @Test
    void signupCreatesUserWithEncodedPasswordAndStoresOnlyRefreshTokenHash() {
        when(appUserRepository.existsByUsername("new_user")).thenReturn(false);
        when(appUserRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Qwer123!")).thenReturn("encoded-password");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(11L);
            return user;
        });

        var response = authService.signup(new SignupRequest("new_user", "new@example.com", "Qwer123!"));

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(jwtService.extractUsername(response.accessToken())).isEqualTo("new_user");
        assertThat(jwtService.extractTokenType(response.accessToken())).isEqualTo("access");

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
        assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.USER);

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().getTokenHash())
                .isNotEqualTo(response.refreshToken())
                .hasSize(64);
        assertThat(tokenCaptor.getValue().getUser().getUsername()).isEqualTo("new_user");
    }

    @Test
    void signupRejectsDuplicateUsername() {
        when(appUserRepository.existsByUsername("user2")).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(new SignupRequest(
                "user2", "another@example.com", "Qwer123!")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void loginRejectsBadCredentialsAsUnauthorized() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("user2", "wrong")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void loginReturnsTokensForValidCredentials() {
        AppUser user = user(2L, "user2", Role.USER);
        when(userDetailsService.loadUserByUsername("user2")).thenReturn(user);

        var response = authService.login(new LoginRequest("user2", "Qwer123!"));

        assertThat(jwtService.extractUsername(response.accessToken())).isEqualTo("user2");
        assertThat(jwtService.extractTokenType(response.accessToken())).isEqualTo("access");
        assertThat(jwtService.extractTokenType(response.refreshToken())).isEqualTo("refresh");
        verify(authenticationManager).authenticate(any());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refreshRevokesOldTokenAndRotatesTokens() {
        AppUser user = user(2L, "user2", Role.USER);
        String oldRefreshToken = jwtService.generateRefreshToken(user);
        RefreshToken storedToken = new RefreshToken(user, sha256(oldRefreshToken), jwtService.extractExpiration(oldRefreshToken));
        when(userDetailsService.loadUserByUsername("user2")).thenReturn(user);
        when(refreshTokenRepository.findByTokenHash(sha256(oldRefreshToken))).thenReturn(Optional.of(storedToken));

        var response = authService.refresh(new RefreshTokenRequest(oldRefreshToken));

        assertThat(storedToken.getRevokedAt()).isNotNull();
        assertThat(jwtService.extractTokenType(response.accessToken())).isEqualTo("access");
        assertThat(jwtService.extractTokenType(response.refreshToken())).isEqualTo("refresh");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refreshRejectsMissingStoredToken() {
        AppUser user = user(2L, "user2", Role.USER);
        String refreshToken = jwtService.generateRefreshToken(user);
        when(userDetailsService.loadUserByUsername("user2")).thenReturn(user);
        when(refreshTokenRepository.findByTokenHash(sha256(refreshToken))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(new RefreshTokenRequest(refreshToken)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void logoutRevokesAllActiveRefreshTokensForUser() {
        AppUser user = user(2L, "user2", Role.USER);
        when(userDetailsService.loadUserByUsername("user2")).thenReturn(user);

        var response = authService.logout("user2");

        assertThat(response.message()).isEqualTo("Successfully logged out");
        verify(refreshTokenRepository).revokeAllActiveByUser(user);
    }

    private static AppUser user(Long id, String username, Role role) {
        AppUser user = new AppUser(username, username + "@example.com", "hash", role);
        user.setId(id);
        return user;
    }

    private static String sha256(String token) {
        try {
            byte[] digest = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (java.security.NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
