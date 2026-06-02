/**
 * cartStore.test.ts — Zustand cart store tests
 */

import { describe, it, expect, beforeEach } from 'vitest';
import { useCartStore } from './cartStore';
import type { Product } from '@/types/product.types';

const mockProduct: Product = {
  id: '11111111-1111-1111-1111-111111111111',
  name: 'Test Laptop',
  description: 'Demo',
  price: 50000,
  stockKeepingUnit: 'SKU-1',
  rating: 4.5,
  categoryId: '22222222-2222-2222-2222-222222222222',
  categoryName: 'Electronics',
};

describe('cartStore', () => {
  beforeEach(() => {
    useCartStore.setState({ items: [] });
  });

  it('addItem increases quantity for same product', () => {
    useCartStore.getState().addItem(mockProduct, 1);
    useCartStore.getState().addItem(mockProduct, 2);
    expect(useCartStore.getState().items[0].quantity).toBe(3);
  });

  it('totalPrice sums line totals', () => {
    useCartStore.getState().addItem(mockProduct, 2);
    expect(useCartStore.getState().totalPrice()).toBe(100000);
  });

  it('removeItem clears product', () => {
    useCartStore.getState().addItem(mockProduct, 1);
    useCartStore.getState().removeItem(mockProduct.id);
    expect(useCartStore.getState().items).toHaveLength(0);
  });
});
