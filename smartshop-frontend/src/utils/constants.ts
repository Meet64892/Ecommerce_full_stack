/**
 * constants.ts — Application-wide constants and route paths
 *
 * PURPOSE:
 * Single source of truth for magic strings (routes, page size, order statuses).
 * Prevents typos in router paths and keeps filter tabs in sync with backend enums.
 *
 * CONNECTED TO:
 * - src/App.tsx, pages, components
 */

import type { OrderStatus } from '@/types/order.types';

export const PAGE_SIZE = 12;

export const ROUTES = {
  HOME: '/',
  PRODUCTS: '/products',
  PRODUCT_DETAIL: '/products/:id',
  CART: '/cart',
  CHECKOUT: '/checkout',
  ORDERS: '/orders',
  ORDER_DETAIL: '/orders/:id',
  PROFILE: '/profile',
  LOGIN: '/login',
  REGISTER: '/register',
  SELL: '/sell',
  SUPER_ADMIN: '/super-admin',
  VENDOR_ADMIN: '/admin',
} as const;

export const SUPER_ADMIN_ROUTES = {
  DASHBOARD: '/super-admin/dashboard',
  BRANDS: '/super-admin/brands',
  PRODUCTS: '/super-admin/products',
  ORDERS: '/super-admin/orders',
  USERS: '/super-admin/users',
  SETTINGS: '/super-admin/settings',
} as const;

export const VENDOR_ROUTES = {
  DASHBOARD: '/admin/dashboard',
  PRODUCTS: '/admin/products',
  ORDERS: '/admin/orders',
  PROMOTIONS: '/admin/promotions',
  REPORTS: '/admin/reports',
} as const;

export const ORDER_STATUS: OrderStatus[] = [
  'PENDING',
  'CONFIRMED',
  'SHIPPED',
  'DELIVERED',
  'CANCELLED',
];

export const ORDER_STATUS_LABELS: Record<OrderStatus, string> = {
  PENDING: 'Pending',
  CONFIRMED: 'Confirmed',
  SHIPPED: 'Shipped',
  DELIVERED: 'Delivered',
  CANCELLED: 'Cancelled',
};

export const SORT_OPTIONS = [
  { value: 'price,asc', label: 'Price: Low to High' },
  { value: 'price,desc', label: 'Price: High to Low' },
  { value: 'createdAt,desc', label: 'Newest' },
  { value: 'rating,desc', label: 'Top Rated' },
] as const;
