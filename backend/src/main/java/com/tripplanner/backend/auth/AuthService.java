package com.tripplanner.backend.auth;

import com.tripplanner.backend.auth.AuthDtos.LoginRequest;
import com.tripplanner.backend.auth.AuthDtos.MessageResponse;
import com.tripplanner.backend.auth.AuthDtos.RefreshTokenRequest;
import com.tripplanner.backend.auth.AuthDtos.SignupRequest;
import com.tripplanner.backend.auth.AuthDtos.TokenResponse;
import com.tripplanner.backend.user.AppUser;
import com.tripplanner.backend.user.AppUserRepository;
import com.tripplanner.backend.user.Role;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (appUserRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        AppUser user = new AppUser(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.USER);
        AppUser savedUser = appUserRepository.save(user);
        return tokensFor(savedUser);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        AppUser user = (AppUser) userDetailsService.loadUserByUsername(request.username());
        return tokensFor(user);
    }

    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        try {
            String refreshToken = request.refreshToken();
            AppUser user = (AppUser) userDetailsService.loadUserByUsername(jwtService.extractUsername(refreshToken));
            RefreshToken storedToken = refreshTokenRepository.findByTokenHash(hashToken(refreshToken))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

            if (!jwtService.isRefreshTokenValid(request.refreshToken(), user)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
            }
            if (!storedToken.isActive()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
            }

            storedToken.revoke();
            return tokensFor(user);
        } catch (IllegalArgumentException | UsernameNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
    }

    @Transactional
    public MessageResponse logout(String username) {
        AppUser user = (AppUser) userDetailsService.loadUserByUsername(username);
        refreshTokenRepository.revokeAllActiveByUser(user);
        return new MessageResponse("Successfully logged out");
    }

    private TokenResponse tokensFor(AppUser user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        refreshTokenRepository.deleteAllByUser(user);
        refreshTokenRepository.save(new RefreshToken(
                user,
                hashToken(refreshToken),
                jwtService.extractExpiration(refreshToken)));
        return new TokenResponse(accessToken, refreshToken);
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }
}
