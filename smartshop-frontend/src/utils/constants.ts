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
  ADMIN: '/admin',
  ADMIN_BRANDS: '/admin/brands',
  ADMIN_PRODUCTS: '/admin/products',
  ADMIN_USERS: '/admin/users',
  ADMIN_ORDERS: '/admin/orders',
  BRAND: '/brand/products',
  VENDOR: '/vendor',
  VENDOR_APPLY: '/vendor/apply',
  VENDOR_ORDERS: '/vendor/orders',
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
