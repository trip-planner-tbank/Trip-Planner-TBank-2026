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
        createIfMissing(adminUsername, adminEmail, adminPassword, Role.ADMIN);
        for (int number = 2; number <= 10; number++) {
            createIfMissing(
                    "user" + number,
                    "user" + number + "@example.com",
                    "Qwer123!",
                    Role.USER);
        }
    }

    private void createIfMissing(String username, String email, String password, Role role) {
        if (!appUserRepository.existsByUsername(username) && !appUserRepository.existsByEmail(email)) {
            appUserRepository.save(new AppUser(username, email, passwordEncoder.encode(password), role));
        }
    }
}
