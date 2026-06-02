package com.smartshop.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest - DTO for Authentication (Login) Requests
 *
 * <h2>Purpose</h2>
 * Carries login credentials from the client to the AuthController.
 * Separate from RegisterRequest to keep the API contract clear — login
 * only needs identifier + password, not all registration fields.
 *
 * @author SmartShop Team
 */
public record LoginRequest(

    /**
     * Email or username — the user can log in with either.
     * The service layer queries: WHERE email = ? OR username = ?
     */
    @NotBlank(message = "Email or username is required")
    String emailOrUsername,

    /**
     * Plain-text password submitted by the user.
     * The service layer calls BCryptPasswordEncoder.matches(plaintext, hash)
     * to verify it against the stored hash WITHOUT ever decrypting the hash.
     * BCrypt is a one-way hash — decryption is computationally infeasible.
     */
    @NotBlank(message = "Password is required")
    String password
) {}
