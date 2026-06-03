/**
 * Button.tsx — Primary action button with variants
 *
 * PURPOSE:
 * Consistent interactive element; demonstrates React.memo for list performance.
 *
 * CONNECTED TO:
 * - Forms, ProductCard, CheckoutPage
 */

import { forwardRef, memo, type ButtonHTMLAttributes } from 'react';
import { cn } from '@utils/cn';
import { Spinner } from './Spinner';

export type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger';
export type ButtonSize = 'sm' | 'md' | 'lg';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  isLoading?: boolean;
}

const variantClasses: Record<ButtonVariant, string> = {
  primary:
    'relative overflow-hidden bg-gradient-accent text-white shadow-glow-sm hover:bg-gradient-accent-hover hover:shadow-glow hover:scale-[1.02] active:scale-[0.98] focus:ring-primary-500/50 btn-shine',
  secondary:
    'border border-slate-600/80 bg-surface-elevated text-slate-100 hover:border-primary-500/50 hover:bg-surface-hover hover:shadow-glow-sm active:scale-[0.98] focus:ring-primary-500/40',
  ghost:
    'bg-transparent text-slate-200 hover:bg-surface-hover hover:text-white active:scale-[0.98]',
  danger:
    'bg-red-600/90 text-white hover:bg-red-500 hover:shadow-[0_0_16px_-4px_rgba(239,68,68,0.5)] active:scale-[0.98] focus:ring-red-500/50',
};

const sizeClasses: Record<ButtonSize, string> = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2 text-sm',
  lg: 'px-6 py-3 text-base',
};

/**
 * Button — Accessible button with loading state
 *
 * LEARNING NOTE: React.memo skips re-render when props are shallow-equal (helps ProductGrid lists).
 */
const ButtonComponent = forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      className,
      variant = 'primary',
      size = 'md',
      isLoading,
      disabled,
      children,
      ...props
    },
    ref,
  ) => (
    <button
      ref={ref}
      type="button"
      disabled={disabled || isLoading}
      className={cn(
        'inline-flex items-center justify-center gap-2 rounded-lg font-medium',
        'transition-all duration-200 ease-spring',
        'focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-surface-muted disabled:cursor-not-allowed disabled:opacity-60 disabled:hover:scale-100',
        variantClasses[variant],
        sizeClasses[size],
        className,
      )}
      {...props}
    >
      {isLoading && <Spinner size="sm" />}
      {children}
    </button>
  ),
);

ButtonComponent.displayName = 'Button';

export const Button = memo(ButtonComponent);
