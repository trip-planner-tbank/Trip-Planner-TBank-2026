package com.tripplanner.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin12345}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (appUserRepository.existsByUsername(adminUsername) || appUserRepository.existsByEmail(adminEmail)) {
            return;
        }

        AppUser admin = new AppUser(
                adminUsername,
                adminEmail,
                passwordEncoder.encode(adminPassword),
                Role.ADMIN);
        appUserRepository.save(admin);
    }
}
