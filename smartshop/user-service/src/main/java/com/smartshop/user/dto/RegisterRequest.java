package com.smartshop.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest - Inbound payload for {@code POST /auth/register}.
 *
 * <h2>Purpose</h2>
 * Captures and validates the data needed to create an account. Implemented as a
 * record because request DTOs are immutable value objects — once parsed they are
 * never mutated, which is safer and clearer.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Bean Validation</b>: the {@code @NotBlank}, {@code @Email}, etc.
 *       annotations are enforced when the controller parameter is annotated
 *       {@code @Valid}. A violation raises {@code MethodArgumentNotValidException}
 *       which the GlobalExceptionHandler turns into a 400 with field details.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Bound by {@code AuthController.register}, mapped into a {@code User} entity.
 *
 * @param email    unique login email (validated as well-formed)
 * @param password plaintext password (validated for strength, then hashed)
 * @param fullName display name
 * @author SmartShop Team
 */
public record RegisterRequest(

        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be a valid address")
        String email,

        @NotBlank(message = "password must not be blank")
        @Size(min = 8, max = 72, message = "password must be 8-72 characters")
        // Require at least one letter and one digit. 72 is BCrypt's max input.
        @Pattern(regexp = ".*[A-Za-z].*\\d.*|.*\\d.*[A-Za-z].*",
                message = "password must contain at least one letter and one digit")
        String password,

        @NotBlank(message = "fullName must not be blank")
        @Size(max = 100, message = "fullName must be at most 100 characters")
        String fullName
) {
}
