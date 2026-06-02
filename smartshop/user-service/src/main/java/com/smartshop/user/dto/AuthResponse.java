package com.smartshop.user.dto;

import com.smartshop.user.entity.Role;
import lombok.Builder;

/**
 * AuthResponse - DTO Returned After Successful Authentication
 *
 * <h2>Purpose</h2>
 * Contains everything the client needs after a successful login or registration:
 *   - The JWT access token (use in Authorization: Bearer [token] header for future requests)
 *   - Basic user information (so the frontend doesn't need a separate /me call)
 *   - Token expiration time (so the frontend knows when to refresh)
 *
 * @author SmartShop Team
 */
@Builder
public record AuthResponse(

    /** The JWT token to include in future API requests */
    String accessToken,

    /** Token type — always "Bearer" for JWT */
    String tokenType,

    /** When this token expires (Unix timestamp in milliseconds) */
    long expiresAt,

    /** The authenticated user's ID */
    Long userId,

    /** The authenticated user's username */
    String username,

    /** The authenticated user's email */
    String email,

    /** The user's role — determines what endpoints they can access */
    Role role
) {
    /**
     * Convenience factory for building AuthResponse with tokenType pre-set to "Bearer".
     * "Bearer" is the token type defined in RFC 6750 for OAuth 2.0 Bearer Tokens.
     * All JWT-based auth follows the Bearer token scheme.
     */
    public static AuthResponse of(String token, long expiresAt, Long userId,
                                   String username, String email, Role role) {
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresAt(expiresAt)
                .userId(userId)
                .username(username)
                .email(email)
                .role(role)
                .build();
    }
}
