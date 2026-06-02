/**
 * cn.ts — Combines clsx + tailwind-merge for conditional class names
 *
 * PURPOSE:
 * `cn('px-2', condition && 'bg-primary-500')` merges Tailwind classes without conflicts.
 */

import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]): string {
  return twMerge(clsx(inputs));
}
