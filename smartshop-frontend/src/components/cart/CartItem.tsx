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
    <div className="flex gap-4 border-b border-gray-100 py-4">
      <div className="flex h-16 w-16 shrink-0 items-center justify-center rounded-lg bg-primary-50 text-xl font-bold text-primary-400">
        {product.name.charAt(0)}
      </div>
      <div className="min-w-0 flex-1">
        <Link to={`/products/${product.id}`} className="font-medium text-gray-900 hover:text-primary-600">
          {product.name}
        </Link>
        <p className="text-sm text-gray-500">{formatCurrency(product.price)} each</p>
        <div className="mt-2 flex items-center gap-2">
          <Button
            size="sm"
            variant="secondary"
            onClick={() => updateQuantity(product.id, quantity - 1)}
            aria-label="Decrease quantity"
          >
            −
          </Button>
          <span className="w-8 text-center text-sm">{quantity}</span>
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
      <p className="font-semibold text-gray-900">{formatCurrency(product.price * quantity)}</p>
    </div>
  );
}
