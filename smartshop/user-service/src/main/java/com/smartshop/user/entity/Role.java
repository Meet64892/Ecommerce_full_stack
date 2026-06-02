package com.smartshop.user.entity;

/**
 * Role - The set of authorization roles a user can hold.
 *
 * <h2>Purpose</h2>
 * Drives role-based access control (RBAC). Spring Security maps these to
 * authorities (prefixed {@code ROLE_}) used in {@code hasRole(...)} checks and
 * method security.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Enum (not free-text strings): the compiler guarantees only valid roles
 *       exist, preventing typos like "ADMINN" that silently break authorization.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Stored on {@link User#getRole()} and embedded as a claim in the JWT so the
 * gateway and downstream services can authorize without a DB round-trip.
 *
 * @author SmartShop Team
 */
public enum Role {
    /** A shopper: can browse, order, view own profile. */
    CUSTOMER,
    /** Platform administrator: full management access. */
    ADMIN,
    /** A merchant who lists/manages products. */
    SELLER
}
