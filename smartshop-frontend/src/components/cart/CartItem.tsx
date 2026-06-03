/**
 * CartItem.tsx — Line item in cart drawer/page
 */

import { Link } from 'react-router-dom';
import type { CartItem as CartItemType } from '@/types/cart.types';
import { formatCurrency } from '@utils/formatters';
import { Button } from '@components/ui/Button';
import { useCart } from '@hooks/useCart';

export function CartItem({ item }: { item: CartItemType }) {
  const { updateQuantity, removeItem } = useCart();
  const { product, quantity } = item;

  return (
    <div className="group flex gap-4 border-b border-slate-700/40 py-4 transition-colors duration-200 hover:bg-surface-hover/30">
      <div className="flex h-16 w-16 shrink-0 items-center justify-center rounded-lg border border-primary-500/30 bg-primary-600/10 text-xl font-bold text-primary-300 transition-all duration-200 group-hover:border-primary-400/50 group-hover:shadow-glow-sm">
        {product.name.charAt(0)}
      </div>
      <div className="min-w-0 flex-1">
        <Link
          to={`/products/${product.id}`}
          className="font-medium text-slate-100 transition-colors hover:text-primary-300"
        >
          {product.name}
        </Link>
        <p className="text-sm text-slate-500">{formatCurrency(product.price)} each</p>
        <div className="mt-2 flex items-center gap-2">
          <Button
            size="sm"
            variant="secondary"
            onClick={() => updateQuantity(product.id, quantity - 1)}
            aria-label="Decrease quantity"
          >
            −
          </Button>
          <span className="w-8 text-center text-sm text-slate-300">{quantity}</span>
          <Button
            size="sm"
            variant="secondary"
            onClick={() => updateQuantity(product.id, quantity + 1)}
            aria-label="Increase quantity"
          >
            +
          </Button>
          <Button size="sm" variant="ghost" onClick={() => removeItem(product.id)}>
            Remove
          </Button>
        </div>
      </div>
      <p className="font-semibold text-slate-100">{formatCurrency(product.price * quantity)}</p>
    </div>
  );
}
