package com.smartshop.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * JwtService - Creates and validates JSON Web Tokens for authenticated users.
 *
 * <h2>Purpose</h2>
 * Stateless authentication lets SmartShop scale horizontally because no server-side HTTP session must be replicated.
 * JWTs contain a header, payload, and signature; the signature prevents clients from modifying claims undetected.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>HS256 vs RS256: HS256 uses one shared secret, while RS256 uses private/public key pairs for larger systems.</li>
 *   <li>Claims: Signed pieces of user metadata such as subject, role, and expiration.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * UserServiceImpl creates tokens after login, JwtAuthFilter validates them on later requests, and the gateway also verifies them.
 *
 * @see com.smartshop.user.security.JwtAuthFilter
 * @author SmartShop Team
 */
@Service
public class JwtService {
    private final SecretKey key;
    private final Duration expiration;

    /**
     * Constructor injection makes token settings explicit and easy to override in tests.
     *
     * @param secret shared HMAC secret; production should load this from a secret manager
     * @param expirationSeconds token lifetime in seconds
     */
    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.expiration-seconds}") long expirationSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofSeconds(expirationSeconds);
    }

    /**
     * Generates a signed JWT for a user.
     *
     * @param email subject claim and login username
     * @param role role claim used by downstream authorization decisions
     * @return compact signed JWT string
     */
    public String generateToken(String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claims(Map.of("role", role))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the username from a signed token after verifying its signature.
     *
     * @param token JWT string from the Authorization header
     * @return subject claim, which is the user's email
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Validates that the token subject matches the loaded user and that the token has not expired.
     *
     * @param token compact JWT
     * @param userDetails Spring Security principal loaded from the database
     * @return true when the token is still trusted for this user
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && parseClaims(token).getExpiration().after(new Date());
    }

    /**
     * Returns the configured token lifetime for API responses.
     *
     * @return expiration duration in seconds
     */
    public long expirationSeconds() {
        return expiration.toSeconds();
    }

    /**
     * Parses signed claims and verifies the token using the configured HMAC key.
     *
     * @param token compact JWT string
     * @return verified claims payload
     */
    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
