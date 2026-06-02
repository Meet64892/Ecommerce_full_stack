package com.smartshop.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest - Payload for account registration.
 *
 * <h2>Purpose</h2>
 * This immutable DTO applies Bean Validation constraints at API boundaries so malformed requests
 * are rejected before service logic executes.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bean Validation: declarative input constraints for contract safety.</li>
 *   <li>Java records: concise immutable carriers suitable for request payloads.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * AuthController receives this DTO and passes it to UserService for registration.
 *
 * @see com.smartshop.user.controller.AuthController
 * @author SmartShop Team
 */
public record RegisterRequest(
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 10, message = "Password must be at least 10 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "Password must include upper, lower, number, and special char")
        String password,
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name max length is 50")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name max length is 50")
        String lastName
) {
}
