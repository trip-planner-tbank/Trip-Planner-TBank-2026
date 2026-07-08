package com.tripplanner.backend.auth;

import com.tripplanner.backend.auth.AuthDtos.LoginRequest;
import com.tripplanner.backend.auth.AuthDtos.MessageResponse;
import com.tripplanner.backend.auth.AuthDtos.RefreshTokenRequest;
import com.tripplanner.backend.auth.AuthDtos.SignupRequest;
import com.tripplanner.backend.auth.AuthDtos.TokenResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public MessageResponse logout(Principal principal) {
        return authService.logout(principal.getName());
    }
}
