package com.smartshop.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest - Inbound payload for {@code POST /auth/login}.
 *
 * <h2>Purpose</h2>
 * Carries credentials to authenticate. Deliberately minimal: only what is needed
 * to verify identity.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Validation here only checks shape (present + email-formatted); the
 *       actual credential check happens against the BCrypt hash in the service.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Bound by {@code AuthController.login}; passed to the authentication flow.
 *
 * @param email    the account email
 * @param password the plaintext password to verify against the stored hash
 * @author SmartShop Team
 */
public record LoginRequest(

        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be a valid address")
        String email,

        @NotBlank(message = "password must not be blank")
        String password
) {
}
