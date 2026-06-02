package com.smartshop.user.entity;

/**
 * Role - User Authorization Roles Enumeration
 *
 * <h2>Purpose</h2>
 * Defines the set of roles a user can have in the system. Roles are used by
 * Spring Security to make authorization decisions: "Can this user access this endpoint?"
 * By using an enum, the compiler enforces that only valid roles can be assigned —
 * impossible to set role = "SUPERUSER" if it's not defined here.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>RBAC (Role-Based Access Control): Users have roles; roles determine permissions.
 *       This is simpler than ABAC (Attribute-Based) but suitable for most e-commerce apps.
 *       Example: @PreAuthorize("hasRole('ADMIN')") — only ADMIN can access this endpoint.</li>
 *   <li>Spring Security convention: Spring adds "ROLE_" prefix internally.
 *       When you say hasRole("ADMIN"), Spring actually checks for "ROLE_ADMIN".
 *       That's why the JWT stores "CUSTOMER", "ADMIN" without the "ROLE_" prefix —
 *       Spring's UserDetails.getAuthorities() adds it via the "ROLE_" + role.name() pattern.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
public enum Role {

    /**
     * Regular customer who can browse products, place orders, and view their own data.
     * Default role assigned on registration.
     */
    CUSTOMER,

    /**
     * Platform administrator with full access to all endpoints including
     * user management, product creation, and order management.
     */
    ADMIN,

    /**
     * Merchant/seller who can create and manage their own product listings.
     * More permissions than CUSTOMER but restricted compared to ADMIN.
     */
    SELLER
}
