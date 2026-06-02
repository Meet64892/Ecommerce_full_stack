/**
 * CartSummary.tsx — Subtotal and checkout CTA
 */

import { Link } from 'react-router-dom';
import { formatCurrency } from '@utils/formatters';
import { ROUTES } from '@utils/constants';
import { cn } from '@utils/cn';

export function CartSummary({ totalItems, totalPrice }: { totalItems: number; totalPrice: number }) {
  return (
    <div className="rounded-xl border border-gray-200 bg-gray-50 p-4">
      <div className="flex justify-between text-sm text-gray-600">
        <span>Items ({totalItems})</span>
        <span className="font-semibold text-gray-900">{formatCurrency(totalPrice)}</span>
      </div>
      <Link
        to={ROUTES.CHECKOUT}
        className={cn(
          'mt-4 flex w-full items-center justify-center rounded-lg bg-primary-600 px-4 py-2',
          'text-sm font-medium text-white hover:bg-primary-700',
        )}
      >
        Proceed to checkout
      </Link>
    </div>
  );
}
