package com.tripplanner.backend.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripplanner.backend.user.AppUser;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final TypeReference<Map<String, Object>> CLAIMS_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;

    @Value("${app.jwt.secret:change-this-development-secret-to-a-long-random-value}")
    private String secret;

    @Value("${app.jwt.access-token-seconds:900}")
    private long accessTokenSeconds;

    @Value("${app.jwt.refresh-token-seconds:604800}")
    private long refreshTokenSeconds;

    public String generateAccessToken(AppUser user) {
        return generateToken(user, "access", accessTokenSeconds);
    }

    public String generateRefreshToken(AppUser user) {
        return generateToken(user, "refresh", refreshTokenSeconds);
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        try {
            Map<String, Object> claims = parseClaims(token);
            return userDetails.getUsername().equals(claims.get("sub"))
                    && "access".equals(claims.get("typ"))
                    && !isExpired(claims);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        try {
            Map<String, Object> claims = parseClaims(token);
            return userDetails.getUsername().equals(claims.get("sub"))
                    && "refresh".equals(claims.get("typ"))
                    && !isExpired(claims);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return (String) parseClaims(token).get("sub");
    }

    public String extractTokenType(String token) {
        return (String) parseClaims(token).get("typ");
    }

    public Instant extractExpiration(String token) {
        Number expiration = (Number) parseClaims(token).get("exp");
        if (expiration == null) {
            throw new IllegalArgumentException("JWT expiration is missing");
        }
        return Instant.ofEpochSecond(expiration.longValue());
    }

    private String generateToken(AppUser user, String tokenType, long expiresInSeconds) {
        try {
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Instant now = Instant.now();
            Map<String, Object> claims = new LinkedHashMap<>();
            claims.put("sub", user.getUsername());
            claims.put("uid", user.getId());
            claims.put("role", user.getRole().name());
            claims.put("typ", tokenType);
            claims.put("iat", now.getEpochSecond());
            claims.put("exp", now.plusSeconds(expiresInSeconds).getEpochSecond());

            String unsignedToken = base64UrlJson(header) + "." + base64UrlJson(claims);
            return unsignedToken + "." + sign(unsignedToken);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate JWT", ex);
        }
    }

    private Map<String, Object> parseClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT format");
            }

            String unsignedToken = parts[0] + "." + parts[1];
            if (!constantTimeEquals(sign(unsignedToken), parts[2])) {
                throw new IllegalArgumentException("Invalid JWT signature");
            }

            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            return objectMapper.readValue(payload, CLAIMS_TYPE);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid JWT", ex);
        }
    }

    private boolean isExpired(Map<String, Object> claims) {
        Number expiration = (Number) claims.get("exp");
        return expiration == null || Instant.now().getEpochSecond() >= expiration.longValue();
    }

    private String base64UrlJson(Object value) throws Exception {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(value));
    }

    private String sign(String unsignedToken) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
    }

    private boolean constantTimeEquals(String expected, String actual) {
        return expected.length() == actual.length()
                && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}
