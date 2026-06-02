package com.smartshop.user.entity;

/**
 * Role - User authorization role enum.
 *
 * <h2>Purpose</h2>
 * Roles provide coarse-grained authorization controls and are encoded into JWT claims so
 * downstream services can enforce access rules without querying the user database.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Role-based access control (RBAC): permissions are grouped by role.</li>
 *   <li>Token claims: role is embedded in JWT for stateless authorization checks.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Persisted in the User entity and propagated by JwtService into signed tokens.
 *
 * @see User
 * @author SmartShop Team
 */
public enum Role {
    CUSTOMER,
    ADMIN,
    SELLER
}
