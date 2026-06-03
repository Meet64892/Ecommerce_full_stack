/**
 * CartSummary.tsx — Subtotal and checkout CTA
 */

import { Link } from 'react-router-dom';
import { formatCurrency } from '@utils/formatters';
import { ROUTES } from '@utils/constants';
import { useUiStore } from '@store/uiStore';
import { cn } from '@utils/cn';

export function CartSummary({ totalItems, totalPrice }: { totalItems: number; totalPrice: number }) {
  const setCartDrawerOpen = useUiStore((s) => s.setCartDrawerOpen);

  return (
    <div className="surface-card p-4">
      <div className="flex justify-between text-sm text-slate-400">
        <span>Items ({totalItems})</span>
        <span className="font-semibold gradient-text">{formatCurrency(totalPrice)}</span>
      </div>
      <Link
        to={ROUTES.CHECKOUT}
        onClick={() => setCartDrawerOpen(false)}
        className={cn(
          'mt-4 flex w-full items-center justify-center rounded-lg bg-gradient-accent px-4 py-2.5',
          'text-sm font-medium text-white shadow-glow-sm transition-all duration-200',
          'hover:scale-[1.02] hover:shadow-glow active:scale-[0.98]',
        )}
      >
        Proceed to checkout
      </Link>
    </div>
  );
}
