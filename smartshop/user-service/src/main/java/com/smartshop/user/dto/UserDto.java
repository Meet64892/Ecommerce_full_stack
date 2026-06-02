package com.smartshop.user.dto;

import com.smartshop.user.entity.Role;

import java.time.Instant;

/**
 * UserDto - Safe Public Representation of a User (Never Exposes passwordHash)
 *
 * <h2>Purpose</h2>
 * This is what gets returned from GET /users/{id} and GET /users endpoints.
 * It intentionally omits passwordHash and other sensitive fields.
 * UserMapper converts User entities to UserDto instances.
 *
 * @author SmartShop Team
 */
public record UserDto(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName,
    Role role,
    boolean enabled,
    Instant createdAt,
    Instant updatedAt
) {}
