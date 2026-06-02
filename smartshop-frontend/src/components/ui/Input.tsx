/**
 * Input.tsx — Styled text input with label and error
 *
 * CONNECTED TO:
 * - LoginForm, RegisterForm, CheckoutPage, ProductSearch
 */

import { forwardRef, type InputHTMLAttributes } from 'react';
import { cn } from '@utils/cn';

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

/**
 * Input — Labeled field wired to react-hook-form via ref
 *
 * LEARNING NOTE: forwardRef lets parent pass ref for uncontrolled register() integration.
 */
export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ className, label, error, id, ...props }, ref) => {
    const inputId = id ?? props.name;
    return (
      <div className="w-full">
        {label && (
          <label htmlFor={inputId} className="mb-1 block text-sm font-medium text-gray-700">
            {label}
          </label>
        )}
        <input
          ref={ref}
          id={inputId}
          className={cn(
            'w-full rounded-lg border border-gray-300 px-3 py-2 text-sm shadow-sm',
            'focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20',
            error && 'border-red-500 focus:border-red-500 focus:ring-red-500/20',
            className,
          )}
          aria-invalid={!!error}
          aria-describedby={error ? `${inputId}-error` : undefined}
          {...props}
        />
        {error && (
          <p id={`${inputId}-error`} className="mt-1 text-sm text-red-600" role="alert">
            {error}
          </p>
        )}
      </div>
    );
  },
);

Input.displayName = 'Input';
