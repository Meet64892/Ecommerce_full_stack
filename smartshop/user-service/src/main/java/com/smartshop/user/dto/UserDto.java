package com.smartshop.user.dto;

import com.smartshop.user.entity.Role;

import java.time.Instant;

/**
 * UserDto - Outbound, safe representation of a user.
 *
 * <h2>Purpose</h2>
 * The API must never expose the {@code User} entity directly. The entity holds
 * the password hash and ORM-managed state; leaking it risks accidentally
 * serializing secrets and couples the public API to the database schema. This
 * DTO exposes only fields clients should see.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>DTO vs entity</b>: separating them lets the schema evolve (rename a
 *       column) without breaking the API contract, and vice versa.</li>
 *   <li>No {@code passwordHash} field — secrets stay server-side by construction.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Produced by {@code UserMapper} from a {@code User} entity and returned by
 * controllers.
 *
 * @param id        surrogate id
 * @param email     login email
 * @param fullName  display name
 * @param role      authorization role
 * @param enabled   whether the account is active
 * @param createdAt creation timestamp
 * @author SmartShop Team
 */
public record UserDto(
        Long id,
        String email,
        String fullName,
        Role role,
        boolean enabled,
        Instant createdAt
) {
}
