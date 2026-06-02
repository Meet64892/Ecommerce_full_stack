package com.smartshop.user.service;

import com.smartshop.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JwtService - Generates, validates, and reads claims from JSON Web Tokens.
 *
 * <h2>Purpose</h2>
 * Encapsulates all token logic so controllers/services never touch the crypto
 * directly. On login it mints a signed token; on each request the gateway (and
 * this service's filter) verify it.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>JWT anatomy</b>: {@code header.payload.signature}, three Base64URL
 *       segments. The header names the algorithm; the payload holds claims
 *       (subject, roles, expiry); the signature proves integrity/authenticity.</li>
 *   <li><b>HS256 vs RS256</b>: HS256 is symmetric — the same secret signs and
 *       verifies (simple; every verifier must hold the secret). RS256 is
 *       asymmetric — a private key signs and a public key verifies (better when
 *       many independent services must verify but must NOT be able to mint
 *       tokens). We use HS256 here for simplicity with a shared secret.</li>
 *   <li><b>Stateless auth</b>: because the signature is self-verifying, no server
 *       session/DB lookup is needed to authenticate each request.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Used by {@code UserServiceImpl} (issue) and {@code JwtAuthFilter} (verify).
 * Shares its secret with the gateway's {@code AuthenticationFilter}.
 *
 * @author SmartShop Team
 */
@Service
public class JwtService {

    /** Symmetric signing key derived from the configured secret. */
    private final SecretKey signingKey;

    /** Token lifetime in milliseconds. */
    private final long expirationMs;

    /**
     * @param secret       HS256 secret (>= 32 bytes for SHA-256 strength)
     * @param expirationMs token validity window in milliseconds
     */
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        // Keys.hmacShaKeyFor validates the key length is adequate for HS256.
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Mints a signed JWT for the given user.
     *
     * @param user the authenticated user to embed in the token
     * @return a compact, URL-safe signed JWT string
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                // Subject = stable principal identifier (email here).
                .subject(user.getEmail())
                // Custom claims downstream services rely on for authorization.
                .claims(Map.of(
                        "userId", user.getId(),
                        "roles", user.getRole().name()))
                .issuedAt(now)
                .expiration(expiry)
                // Sign so any tampering invalidates the signature.
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extracts the subject (email) from a token, verifying the signature.
     *
     * @param token the JWT
     * @return the subject claim
     * @throws io.jsonwebtoken.JwtException if invalid/expired/tampered
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Validates that the token is well-formed, correctly signed, unexpired, and
     * belongs to the expected user.
     *
     * @param token            the JWT to validate
     * @param expectedUsername the username we expect the token to represent
     * @return true if the token is valid for that user
     */
    public boolean isTokenValid(String token, String expectedUsername) {
        try {
            Claims claims = parseClaims(token);
            // Subject must match AND the token must not be expired.
            return claims.getSubject().equals(expectedUsername)
                    && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            // Any parsing/signature/expiry problem means "not valid".
            return false;
        }
    }

    /**
     * @return token lifetime in ms (exposed for AuthResponse.expiresInMs)
     */
    public long getExpirationMs() {
        return expirationMs;
    }

    /**
     * Parses and signature-verifies the token, returning its claims.
     *
     * @param token the JWT
     * @return the verified claims payload
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
