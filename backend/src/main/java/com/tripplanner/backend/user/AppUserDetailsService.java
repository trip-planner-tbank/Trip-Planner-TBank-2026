package com.tripplanner.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(usernameOrEmail)
                .or(() -> appUserRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
