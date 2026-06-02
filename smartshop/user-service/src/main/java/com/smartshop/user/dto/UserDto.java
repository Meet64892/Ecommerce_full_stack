package com.smartshop.user.dto;

import com.smartshop.user.entity.Role;

/**
 * UserDto - Safe user projection returned to clients.
 *
 * <h2>Purpose</h2>
 * DTOs avoid exposing internal entity fields like password hashes and audit internals. This
 * protects API contracts from accidental persistence refactors.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>API encapsulation: domain entities are internal implementation details.</li>
 *   <li>Immutability: record prevents accidental response mutation.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Mapper converts User entities into this DTO for controllers.
 *
 * @see com.smartshop.user.mapper.UserMapper
 * @author SmartShop Team
 */
public record UserDto(Long id, String email, String firstName, String lastName, Role role) {
}
