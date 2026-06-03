/**
 * Badge.tsx — Small status/count pill
 */

import { cn } from '@utils/cn';
import type { HTMLAttributes } from 'react';

export interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: 'primary' | 'gray' | 'success' | 'warning';
}

const variants = {
  primary: 'bg-gradient-accent text-white shadow-glow-sm animate-pulse-glow',
  gray: 'bg-surface-hover text-slate-300 border border-slate-600/50',
  success: 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30',
  warning: 'bg-amber-500/20 text-amber-300 border border-amber-500/30',
};

export function Badge({ className, variant = 'primary', children, ...props }: BadgeProps) {
  return (
    <span
      className={cn(
        'inline-flex min-w-[1.25rem] items-center justify-center rounded-full px-2 py-0.5 text-xs font-semibold transition-transform duration-200',
        variants[variant],
        className,
      )}
      {...props}
    >
      {children}
    </span>
  );
}
