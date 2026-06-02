package com.smartshop.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest - Immutable payload for user authentication.
 *
 * <h2>Purpose</h2>
 * Login requires only an email and password, so a dedicated DTO keeps the endpoint contract minimal. Validation
 * catches missing credentials before Spring Security attempts authentication.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>DTO boundary: API input is intentionally smaller than the User entity.</li>
 *   <li>Validation flow: MethodArgumentNotValidException is converted by GlobalExceptionHandler.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * AuthController accepts this payload and UserService authenticates it with AuthenticationManager.
 *
 * @see RegisterRequest
 * @author SmartShop Team
 */
public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
}
