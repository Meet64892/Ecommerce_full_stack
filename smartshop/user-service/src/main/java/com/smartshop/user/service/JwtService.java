package com.smartshop.user.service;

import com.smartshop.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * JwtService - JWT token generation and validation helper.
 *
 * <h2>Purpose</h2>
 * Handles token signing and claim extraction in one dedicated component.
 * JWT tokens have three parts: header, payload, and signature. We use HS256 (HMAC) in this demo;
 * production systems often use RS256 with asymmetric keys for safer key distribution.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Token integrity: signature protects claims from tampering.</li>
 *   <li>Expiration: limits blast radius of leaked tokens.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Called by auth flows and JwtAuthFilter to issue/verify bearer tokens.
 *
 * @see com.smartshop.user.security.JwtAuthFilter
 * @author SmartShop Team
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationSeconds;

    /**
     * Initializes service with configured signing key and expiration.
     *
     * @param secret shared HMAC key
     * @param expirationSeconds expiration in seconds
     */
    public JwtService(@Value("${security.jwt.secret}") final String secret,
                      @Value("${security.jwt.expiration-seconds}") final long expirationSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    /**
     * Generates signed JWT for a principal.
     *
     * @param email token subject
     * @param role user role claim
     * @return signed compact token string
     */
    public String generateToken(final String email, final Role role) {
        final Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();
    }

    /**
     * Extracts token claims after signature verification.
     *
     * @param token bearer token without prefix
     * @return validated claims
     */
    public Claims extractClaims(final String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    /**
     * Validates signature and expiry, then checks subject.
     *
     * @param token token to validate
     * @param email expected subject
     * @return true when token is valid for subject
     */
    public boolean validateToken(final String token, final String email) {
        final Claims claims = extractClaims(token);
        return claims.getSubject().equals(email) && claims.getExpiration().after(new Date());
    }
}
