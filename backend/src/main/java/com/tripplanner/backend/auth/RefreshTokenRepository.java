package com.tripplanner.backend.auth;

import com.tripplanner.backend.user.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            update RefreshToken token
            set token.revokedAt = current_timestamp
            where token.user = :user and token.revokedAt is null
            """)
    void revokeAllActiveByUser(@Param("user") AppUser user);
}
