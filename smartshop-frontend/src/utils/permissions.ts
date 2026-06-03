/**
 * permissions.ts — RBAC permission keys for the multi-vendor marketplace
 *
 * Super Admin → full platform
 * Admin (vendor) → own brand catalog and orders
 * User → shopping and profile
 */

import type { UserRole } from '@/types/product.types';

export const PERMISSIONS = {
  // Super Admin
  PLATFORM_DASHBOARD: 'platform:dashboard',
  MANAGE_VENDORS: 'platform:vendors',
  APPROVE_VENDORS: 'platform:vendors:approve',
  MANAGE_ALL_PRODUCTS: 'platform:products',
  APPROVE_PRODUCTS: 'platform:products:approve',
  MANAGE_ALL_ORDERS: 'platform:orders',
  MANAGE_CUSTOMERS: 'platform:customers',
  PLATFORM_FINANCE: 'platform:finance',
  PLATFORM_SETTINGS: 'platform:settings',

  // Vendor Admin
  VENDOR_DASHBOARD: 'vendor:dashboard',
  MANAGE_OWN_PRODUCTS: 'vendor:products',
  MANAGE_OWN_ORDERS: 'vendor:orders',
  VENDOR_PROMOTIONS: 'vendor:promotions',
  VENDOR_REPORTS: 'vendor:reports',

  // Customer
  SHOP: 'user:shop',
  CART: 'user:cart',
  CHECKOUT: 'user:checkout',
  OWN_ORDERS: 'user:orders',
  OWN_PROFILE: 'user:profile',
  APPLY_VENDOR: 'user:vendor:apply',
} as const;

export type Permission = (typeof PERMISSIONS)[keyof typeof PERMISSIONS];

const ROLE_PERMISSIONS: Record<UserRole, Permission[]> = {
  SUPER_ADMIN: [
    PERMISSIONS.PLATFORM_DASHBOARD,
    PERMISSIONS.MANAGE_VENDORS,
    PERMISSIONS.APPROVE_VENDORS,
    PERMISSIONS.MANAGE_ALL_PRODUCTS,
    PERMISSIONS.APPROVE_PRODUCTS,
    PERMISSIONS.MANAGE_ALL_ORDERS,
    PERMISSIONS.MANAGE_CUSTOMERS,
    PERMISSIONS.PLATFORM_FINANCE,
    PERMISSIONS.PLATFORM_SETTINGS,
    PERMISSIONS.SHOP,
    PERMISSIONS.CART,
    PERMISSIONS.CHECKOUT,
    PERMISSIONS.OWN_ORDERS,
    PERMISSIONS.OWN_PROFILE,
  ],
  ADMIN: [
    PERMISSIONS.VENDOR_DASHBOARD,
    PERMISSIONS.MANAGE_OWN_PRODUCTS,
    PERMISSIONS.MANAGE_OWN_ORDERS,
    PERMISSIONS.VENDOR_PROMOTIONS,
    PERMISSIONS.VENDOR_REPORTS,
    PERMISSIONS.SHOP,
    PERMISSIONS.OWN_PROFILE,
  ],
  USER: [
    PERMISSIONS.SHOP,
    PERMISSIONS.CART,
    PERMISSIONS.CHECKOUT,
    PERMISSIONS.OWN_ORDERS,
    PERMISSIONS.OWN_PROFILE,
    PERMISSIONS.APPLY_VENDOR,
  ],
};

export function hasPermission(role: UserRole | undefined, permission: Permission): boolean {
  if (!role) return false;
  return ROLE_PERMISSIONS[role].includes(permission);
}
