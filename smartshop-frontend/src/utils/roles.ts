/**
 * roles.ts — Role helpers for the three-tier access model
 *
 * SUPER_ADMIN: Platform operators (manage everything)
 * SUPER_USER:  Company/brand owners (manage their products)
 * USER:        Customers (browse and purchase)
 */

import type { UserRole } from '@/types/product.types';

/** Maps backend roles to marketplace personas from the product spec */
export const ROLE_LABELS: Record<UserRole, string> = {
  USER: 'Customer',
  SUPER_USER: 'Admin (Vendor)',
  SUPER_ADMIN: 'Super Admin',
};

export function isSuperAdmin(role?: UserRole): boolean {
  return role === 'SUPER_ADMIN';
}

export function isSuperUser(role?: UserRole): boolean {
  return role === 'SUPER_USER';
}

export function isCustomer(role?: UserRole): boolean {
  return role === 'USER';
}

/** Brand owners and platform admins can manage catalog products */
export function canManageProducts(role?: UserRole): boolean {
  return role === 'SUPER_USER' || role === 'SUPER_ADMIN';
}

export function hasAnyRole(role: UserRole | undefined, allowed: UserRole[]): boolean {
  return !!role && allowed.includes(role);
}
