/**
 * roles.ts — Role helpers (Super Admin > Admin/vendor > User)
 */

import type { UserRole } from '@/types/product.types';
import { PERMISSIONS, hasPermission, type Permission } from '@utils/permissions';

export const ROLE_LABELS: Record<UserRole, string> = {
  USER: 'Customer',
  ADMIN: 'Brand admin',
  SUPER_ADMIN: 'Super admin',
};

export function isSuperAdmin(role?: UserRole): boolean {
  return role === 'SUPER_ADMIN';
}

export function isVendorAdmin(role?: UserRole): boolean {
  return role === 'ADMIN';
}

export function isCustomer(role?: UserRole): boolean {
  return role === 'USER';
}

export function canAccessSuperAdminPanel(role?: UserRole): boolean {
  return isSuperAdmin(role);
}

export function canAccessVendorPanel(role?: UserRole): boolean {
  return isVendorAdmin(role) || isSuperAdmin(role);
}

export function canManageProducts(role?: UserRole): boolean {
  return hasPermission(role, PERMISSIONS.MANAGE_OWN_PRODUCTS) || hasPermission(role, PERMISSIONS.MANAGE_ALL_PRODUCTS);
}

export function hasAnyRole(role: UserRole | undefined, allowed: UserRole[]): boolean {
  return !!role && allowed.includes(role);
}

export function can(role: UserRole | undefined, permission: Permission): boolean {
  return hasPermission(role, permission);
}
