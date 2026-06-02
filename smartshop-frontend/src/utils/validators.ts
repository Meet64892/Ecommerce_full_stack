/**
 * validators.ts — Reusable form validation helpers
 *
 * PURPOSE:
 * Small validators complement Zod schemas in forms (e.g. quick checks outside react-hook-form).
 *
 * CONNECTED TO:
 * - LoginForm, RegisterForm, CheckoutPage (Zod is primary; these are extras)
 */

export const isValidEmail = (email: string): boolean =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

/** Indian 6-digit pincode */
export const isValidPincode = (pincode: string): boolean => /^\d{6}$/.test(pincode);

/** Indian mobile: starts 6-9, 10 digits total */
export const isValidIndianPhone = (phone: string): boolean => /^[6-9]\d{9}$/.test(phone);

export const minLength = (value: string, min: number): boolean => value.trim().length >= min;
