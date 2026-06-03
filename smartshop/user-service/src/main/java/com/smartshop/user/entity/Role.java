package com.smartshop.user.entity;

/**
 * Role - Enumerates authorization roles for SmartShop users.
 *
 * <h2>Purpose</h2>
 * Roles give the authorization layer stable names for coarse-grained permissions. Keeping them in an enum prevents
 * typo-driven security bugs that can happen with free-form strings.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>USER: Customer who browses products and places orders.</li>
 *   <li>SUPER_USER: Company/brand owner who manages their catalog products.</li>
 *   <li>SUPER_ADMIN: Platform operator with full administrative access.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * User entities store a role, SecurityConfig maps it to authorities, and controllers can protect endpoints later.
 *
 * @see User
 * @author SmartShop Team
 */
public enum Role {
    USER,
    SUPER_USER,
    SUPER_ADMIN
}
