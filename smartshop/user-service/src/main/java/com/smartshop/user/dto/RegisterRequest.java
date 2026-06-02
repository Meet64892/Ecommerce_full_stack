package com.smartshop.user.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

/**
 * RegisterRequest - DTO for User Registration Requests
 *
 * <h2>Purpose</h2>
 * Captures and validates the input data for new user registration.
 * Using a dedicated DTO (Data Transfer Object) instead of passing the User
 * entity directly prevents clients from setting fields they shouldn't
 * (e.g., role=ADMIN, enabled=true, createdAt).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bean Validation (Jakarta Validation): Annotations like @NotNull, @Email, @Size
 *       are part of the Jakarta Bean Validation 3.0 spec (formerly Java EE).
 *       Spring's @Valid triggers validation before the controller method runs.
 *       If any constraint fails, a MethodArgumentNotValidException is thrown
 *       and caught by GlobalExceptionHandler, which returns HTTP 400.</li>
 *   <li>Why validate at the DTO layer? Catch invalid input as early as possible.
 *       If we let invalid data reach the service layer, we've wasted database connections.
 *       If we let it reach the DB, we might get cryptic SQL errors instead of clear messages.</li>
 *   <li>Java Record: Records (Java 16+) are immutable data carriers. They auto-generate
 *       constructor, getters, equals(), hashCode(), and toString(). Perfect for DTOs
 *       because request data should not change after parsing — immutability is safe by design.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
// Java record: compact immutable data carrier
// Equivalent to a class with final fields, all-args constructor, and auto-generated accessors
public record RegisterRequest(

    /**
     * Desired username — must be 3-50 characters, alphanumeric with underscores/hyphens.
     * @Pattern enforces: starts with letter, alphanumeric or _/-, 3-50 chars total
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(
        regexp = "^[a-zA-Z][a-zA-Z0-9_-]{2,49}$",
        message = "Username must start with a letter and contain only letters, numbers, underscores, and hyphens"
    )
    String username,

    /**
     * Email address — must be a valid RFC 5321 email format.
     * @Email uses Hibernate Validator's email regex.
     * This will be validated as UNIQUE against the DB in the service layer
     * (can't check uniqueness here — it requires a DB query).
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,

    /**
     * Plain-text password — will be BCrypt-hashed before storage.
     * Requirements: 8-100 chars, at least one uppercase, one lowercase, one digit.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    String password,

    /** Optional first name for the user's profile */
    @Size(max = 50, message = "First name must not exceed 50 characters")
    String firstName,

    /** Optional last name for the user's profile */
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    String lastName
) {}
