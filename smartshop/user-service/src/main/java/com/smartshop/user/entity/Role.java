package com.smartshop.user.entity;

/**
 * Role - Marketplace authorization roles.
 *
 * <ul>
 *   <li>USER: Customer — browse, cart, checkout, orders.</li>
 *   <li>ADMIN: Brand/vendor owner — manage own catalog and brand orders.</li>
 *   <li>SUPER_ADMIN: Platform operator — full marketplace control.</li>
 * </ul>
 */
public enum Role {
    USER,
    ADMIN,
    SUPER_ADMIN
}
