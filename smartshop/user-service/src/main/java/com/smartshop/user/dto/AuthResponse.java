package com.smartshop.user.dto;

/**
 * AuthResponse - Login/register response containing a JWT and user profile.
 *
 * <h2>Purpose</h2>
 * Clients need both the signed token for subsequent requests and a safe user summary for immediate UI state. Keeping
 * them together avoids an extra `/me` call after login.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bearer token: Clients send the JWT in the Authorization header.</li>
 *   <li>Token expiry: The token is intentionally time-limited to reduce risk after leakage.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * AuthController returns this record after UserService creates or authenticates an account.
 *
 * @see UserDto
 * @author SmartShop Team
 */
public record AuthResponse(String token, String tokenType, long expiresInSeconds, UserDto user) {
}
