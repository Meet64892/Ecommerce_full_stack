package com.smartshop.user.service;

import com.smartshop.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtService - JWT Token Generation, Validation, and Claims Extraction
 *
 * <h2>Purpose</h2>
 * This service is the ONLY place in the entire platform that creates JWTs.
 * It encapsulates all JWT operations — generation, validation, and claim extraction —
 * so that the calling code (UserServiceImpl) doesn't need to know JWT internals.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>JWT Structure (Header.Payload.Signature):
 *     <pre>
 *     eyJhbGciOiJIUzI1NiJ9                         ← Header (base64): {"alg":"HS256"}
 *     .eyJzdWIiOiIxMjMiLCJyb2xlcyI6IkNVU1RPTUVSII}  ← Payload (base64): claims JSON
 *     .SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  ← Signature: HMAC-SHA256(header+payload, key)
 *     </pre>
 *     The signature mathematically ties the header and payload to the secret key.
 *     Changing ANY bit in the header or payload breaks the signature → forgery detected.</li>
 *   <li>Claims: Key-value pairs in the JWT payload. Standard claims:
 *     - sub (subject): the user ID
 *     - iat (issued at): when the token was created
 *     - exp (expiration): when the token expires
 *     Custom claims: email, roles, firstName — anything the client needs to avoid extra API calls</li>
 *   <li>HS256 (HMAC-SHA256): Symmetric signing — same key signs and verifies.
 *     All services (user-service and api-gateway) share the secret key via config-server.
 *     For production with many services, prefer RS256 (asymmetric) — only user-service
 *     has the private key; others only need the public key for verification.</li>
 *   <li>Token Expiration: JWTs are stateless — you can't "logout" them server-side.
 *     The exp claim causes the token to become invalid at a specific time.
 *     If a token is compromised, you can only wait for it to expire.
 *     Short-lived tokens (15 min) + refresh tokens is the recommended pattern.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret:SmartShopJwtSecretKey2024VeryLongAndSecureKeyForHS256Algorithm}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    /**
     * Generates a signed JWT token for the authenticated user.
     * The token embeds userId, email, username, and role so downstream services
     * can identify the user without querying user-service.
     *
     * @param user the authenticated user to generate a token for
     * @return a signed JWT string ready for the Authorization header
     */
    public String generateToken(User user) {
        // Custom claims embedded in the token payload
        // These are available to any service that can read the JWT (e.g., api-gateway)
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());
        // Store role as string — avoids deserialization issues across services
        claims.put("roles", user.getRole().name());
        claims.put("firstName", user.getFirstName());

        long nowMs = System.currentTimeMillis();

        return Jwts.builder()
                // sub (subject): conventionally the user's unique identifier
                .subject(String.valueOf(user.getId()))
                // Add all custom claims to the payload
                .claims(claims)
                // iat (issued at): when the token was created
                .issuedAt(new Date(nowMs))
                // exp (expiration): after this time, the token is rejected
                .expiration(new Date(nowMs + jwtExpirationMs))
                // Sign with HS256 using our secret key
                // Keys.hmacShaKeyFor converts a byte array to a HMAC-SHA key
                .signWith(getSigningKey())
                .compact();  // Serialize to the base64url.base64url.base64url format
    }

    /**
     * Extracts all claims from a valid JWT token.
     * Throws JwtException subclasses on any failure:
     * - ExpiredJwtException if the token's exp claim has passed
     * - SignatureException if the signature doesn't match (token was tampered)
     * - MalformedJwtException if the token format is invalid
     *
     * @param token the raw JWT string
     * @return the Claims object containing all token payload fields
     * @throws JwtException on any validation failure
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the user ID (subject claim) from the token.
     *
     * @param token the raw JWT string
     * @return the user ID as a Long
     * @throws JwtException if the token is invalid
     */
    public Long extractUserId(String token) {
        String subject = extractAllClaims(token).getSubject();
        return Long.parseLong(subject);
    }

    /**
     * Checks if a token is valid for the given user.
     * Validates:
     *   1. The token's subject (userId) matches the given user
     *   2. The token has not expired
     *
     * @param token the JWT token to validate
     * @param user  the user to validate against
     * @return true if the token is valid and belongs to this user
     */
    public boolean isTokenValid(String token, User user) {
        try {
            Claims claims = extractAllClaims(token);
            Long tokenUserId = Long.parseLong(claims.getSubject());
            // Expiration is already checked by parseSignedClaims() — if expired, it throws
            return tokenUserId.equals(user.getId());
        } catch (JwtException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns the expiration time of the token in milliseconds (Unix timestamp).
     * Used to include the expiry in the AuthResponse so clients know when to refresh.
     *
     * @param token the JWT string
     * @return the expiration timestamp in milliseconds
     */
    public long getTokenExpirationMs(String token) {
        return extractAllClaims(token).getExpiration().getTime();
    }

    /**
     * Builds the HMAC-SHA256 signing key from the configured secret string.
     * Called on every token creation/validation — the key is derived each time.
     * In production, consider caching this key object (it's immutable once created).
     *
     * @return the SecretKey for JWT signing and verification
     */
    private SecretKey getSigningKey() {
        // The secret must be >= 256 bits (32 characters) for HS256
        // Keys.hmacShaKeyFor validates the key length and throws if insufficient
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
