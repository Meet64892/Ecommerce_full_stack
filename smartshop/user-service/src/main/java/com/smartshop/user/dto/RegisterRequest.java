package com.smartshop.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest - Immutable payload for account registration.
 *
 * <h2>Purpose</h2>
 * DTOs keep external API shapes separate from database entities, preventing accidental exposure of fields such as
 * passwordHash. Bean Validation annotations reject malformed input before service logic runs.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bean Validation: Jakarta annotations produce ConstraintViolation errors handled centrally.</li>
 *   <li>Record immutability: Request values cannot be changed after construction.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * AuthController receives this record, validates it, and passes it to UserService for registration.
 *
 * @see AuthResponse
 * @author SmartShop Team
 */
public record RegisterRequest(
        @NotBlank @Email @Size(max = 320) String email,
        @NotBlank @Size(min = 8, max = 72) @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$", message = "password must include upper, lower, and digit") String password,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName) {
}
