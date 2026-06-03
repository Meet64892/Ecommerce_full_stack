/**
 * Badge.tsx — Small status/count pill
 */

import { cn } from '@utils/cn';
import type { HTMLAttributes } from 'react';

export interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: 'primary' | 'gray' | 'success' | 'warning';
}

const variants = {
  primary: 'bg-white text-neutral-950',
  gray: 'bg-neutral-800 text-neutral-200',
  success: 'bg-neutral-200 text-neutral-950',
  warning: 'bg-neutral-600 text-white',
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
