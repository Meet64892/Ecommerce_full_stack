package com.smartshop.user.dto;

import com.smartshop.user.entity.Role;

import java.time.Instant;
import java.util.UUID;

/**
 * UserDto - Public representation of a SmartShop user.
 *
 * <h2>Purpose</h2>
 * DTOs prevent controllers from exposing persistence internals and sensitive data. This DTO intentionally omits
 * passwordHash and keeps only fields clients are allowed to see.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Projection: A safe subset of entity fields for API output.</li>
 *   <li>Records: Java creates constructor, accessors, equals, and hashCode automatically.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * UserMapper converts User entities to this record for controllers and AuthResponse.
 *
 * @see com.smartshop.user.mapper.UserMapper
 * @author SmartShop Team
 */
public record UserDto(UUID id, String email, String firstName, String lastName, String fullName, Role role, UUID brandId,
                      Instant createdAt) {
}
