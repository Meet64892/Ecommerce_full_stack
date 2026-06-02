/**
 * useCart.ts — Cart operations with optional toast feedback
 *
 * CONNECTED TO:
 * - ProductCard, CartDrawer, Header badge
 */

import { useCallback } from 'react';
import toast from 'react-hot-toast';
import { useCartStore } from '@store/cartStore';
import type { Product } from '@/types/product.types';
import { formatCurrency } from '@utils/formatters';

export function useCart() {
  const items = useCartStore((s) => s.items);
  const addItem = useCartStore((s) => s.addItem);
  const removeItem = useCartStore((s) => s.removeItem);
  const updateQuantity = useCartStore((s) => s.updateQuantity);
  const clearCart = useCartStore((s) => s.clearCart);
  const totalItems = useCartStore((s) => s.totalItems);
  const totalPrice = useCartStore((s) => s.totalPrice);
  const isInCart = useCartStore((s) => s.isInCart);

  const addToCart = useCallback(
    (product: Product, quantity = 1, options?: { silent?: boolean }) => {
      addItem(product, quantity);
      if (!options?.silent) {
        toast.success(`Added ${product.name} (${formatCurrency(product.price)})`);
      }
    },
    [addItem],
  );

  return {
    items,
    addToCart,
    removeItem,
    updateQuantity,
    clearCart,
    totalItems: totalItems(),
    totalPrice: totalPrice(),
    isInCart,
  };
}
