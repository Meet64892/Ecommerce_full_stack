package com.smartshop.user.dto;

/**
 * AuthResponse - Login/register response containing JWT and principal data.
 *
 * <h2>Purpose</h2>
 * Bundles token and user profile information to reduce extra client round-trips immediately
 * after authentication.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>JWT bearer token: stateless credential sent in Authorization header.</li>
 *   <li>Self-contained response: includes principal snapshot for UX convenience.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * AuthController returns this object after successful register/login.
 *
 * @see com.smartshop.user.service.JwtService
 * @author SmartShop Team
 */
public record AuthResponse(String token, UserDto user) {
}
