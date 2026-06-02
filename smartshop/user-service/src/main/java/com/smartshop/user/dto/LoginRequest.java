package com.smartshop.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest - Payload for authentication attempts.
 *
 * <h2>Purpose</h2>
 * Captures user credentials in a minimal immutable request model.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Input minimization: only required fields are accepted.</li>
 *   <li>Validation first: rejected before reaching auth provider if malformed.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * AuthController validates this DTO and delegates to UserService login flow.
 *
 * @see RegisterRequest
 * @author SmartShop Team
 */
public record LoginRequest(
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Password is required")
        String password
) {
}
