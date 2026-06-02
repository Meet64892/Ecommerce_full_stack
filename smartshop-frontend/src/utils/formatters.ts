/**
 * formatters.ts — Pure formatting helpers for UI display
 *
 * PURPOSE:
 * Keeps presentation logic out of components. Pure functions are easy to unit test and reuse.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Intl.NumberFormat: Browser-native i18n for currency (no extra library).
 * - date-fns: Tree-shakeable date utilities vs heavy Moment.js.
 *
 * CONNECTED TO:
 * - ProductCard, OrderCard, CartSummary, etc.
 */

import { formatDistanceToNow, parseISO } from 'date-fns';
import type { OrderStatus } from '@/types/order.types';
import { ORDER_STATUS_LABELS } from './constants';

/**
 * formatCurrency — Formats a number as Indian Rupees (INR)
 *
 * LEARNING NOTE: Intl APIs respect locale rules (symbol position, grouping).
 *
 * @param amount - Numeric price from API (BigDecimal deserialized as number)
 */
export const formatCurrency = (amount: number): string =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(amount);

/**
 * formatRelativeTime — Human-readable relative time (e.g. "3 days ago")
 *
 * @param date - ISO-8601 string from backend Instant
 */
export const formatRelativeTime = (date: string): string =>
  formatDistanceToNow(parseISO(date), { addSuffix: true });

/** Maps backend OrderStatus enum to shopper-friendly label */
export const formatOrderStatus = (status: OrderStatus): string =>
  ORDER_STATUS_LABELS[status] ?? status;

/**
 * truncate — Shortens long product descriptions in cards
 *
 * @param text - Full description
 * @param maxLength - Character limit before ellipsis
 */
export const truncate = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) return text;
  return `${text.slice(0, maxLength).trim()}…`;
};

/** formatPhone — Groups Indian mobile for display */
export const formatPhone = (phone: string): string => {
  const digits = phone.replace(/\D/g, '');
  if (digits.length === 10) {
    return `${digits.slice(0, 5)} ${digits.slice(5)}`;
  }
  return phone;
};
