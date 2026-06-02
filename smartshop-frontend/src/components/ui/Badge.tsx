/**
 * Badge.tsx — Small status/count pill
 */

import { cn } from '@utils/cn';
import type { HTMLAttributes } from 'react';

export interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: 'primary' | 'gray' | 'success' | 'warning';
}

const variants = {
  primary: 'bg-primary-100 text-primary-800',
  gray: 'bg-gray-100 text-gray-800',
  success: 'bg-green-100 text-green-800',
  warning: 'bg-amber-100 text-amber-800',
};

export function Badge({ className, variant = 'primary', children, ...props }: BadgeProps) {
  return (
    <span
      className={cn(
        'inline-flex min-w-[1.25rem] items-center justify-center rounded-full px-2 py-0.5 text-xs font-semibold',
        variants[variant],
        className,
      )}
      {...props}
    >
      {children}
    </span>
  );
}
