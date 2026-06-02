/**
 * Tooltip.tsx — Simple hover tooltip
 */

import { useState, type ReactNode } from 'react';
import { cn } from '@utils/cn';

export function Tooltip({ content, children }: { content: string; children: ReactNode }) {
  const [open, setOpen] = useState(false);
  return (
    <span
      className="relative inline-flex"
      onMouseEnter={() => setOpen(true)}
      onMouseLeave={() => setOpen(false)}
      onFocus={() => setOpen(true)}
      onBlur={() => setOpen(false)}
    >
      {children}
      {open && (
        <span
          role="tooltip"
          className={cn(
            'absolute bottom-full left-1/2 z-50 mb-1 -translate-x-1/2 whitespace-nowrap',
            'rounded bg-gray-900 px-2 py-1 text-xs text-white',
          )}
        >
          {content}
        </span>
      )}
    </span>
  );
}
