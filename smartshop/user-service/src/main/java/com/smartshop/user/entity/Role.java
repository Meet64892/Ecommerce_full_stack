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
 *   <li>CUSTOMER: A shopper who can browse products and place orders.</li>
 *   <li>ADMIN/SELLER: Elevated roles that can manage users or catalog data.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * User entities store a role, SecurityConfig maps it to authorities, and controllers can protect endpoints later.
 *
 * @see User
 * @author SmartShop Team
 */
public enum Role {
    CUSTOMER,
    ADMIN,
    SELLER
}
