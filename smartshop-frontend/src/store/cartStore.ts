/**
 * cartStore.ts — Persistent shopping cart with Immer for nested updates
 *
 * PURPOSE:
 * Client-side cart until checkout POST /orders. Survives refresh via persist middleware.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - immer middleware: Write `state.items.push(...)` style updates with immutable output.
 * - Derived state: totalItems/totalPrice computed from items — never stored separately (avoids drift).
 * - Stale cart risk: Prices/stock may change server-side; checkout re-validates on the backend.
 *
 * CONNECTED TO:
 * - ProductCard, CartDrawer, CheckoutPage
 */

import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { immer } from 'zustand/middleware/immer';
import type { Product } from '@/types/product.types';
import type { CartItem } from '@/types/cart.types';

interface CartState {
  items: CartItem[];
  addItem: (product: Product, quantity: number) => void;
  removeItem: (productId: string) => void;
  updateQuantity: (productId: string, quantity: number) => void;
  clearCart: () => void;
  totalItems: () => number;
  totalPrice: () => number;
  isInCart: (productId: string) => boolean;
}

export const useCartStore = create<CartState>()(
  persist(
    immer((set, get) => ({
      items: [],

      addItem: (product, quantity) => {
        set((state) => {
          const existing = state.items.find((i) => i.product.id === product.id);
          if (existing) {
            existing.quantity += quantity;
          } else {
            state.items.push({ product, quantity });
          }
        });
      },

      removeItem: (productId) => {
        set((state) => {
          state.items = state.items.filter((i) => i.product.id !== productId);
        });
      },

      updateQuantity: (productId, quantity) => {
        if (quantity < 1) {
          get().removeItem(productId);
          return;
        }
        set((state) => {
          const item = state.items.find((i) => i.product.id === productId);
          if (item) item.quantity = quantity;
        });
      },

      clearCart: () => set({ items: [] }),

      totalItems: () => get().items.reduce((sum, i) => sum + i.quantity, 0),

      totalPrice: () =>
        get().items.reduce((sum, i) => sum + i.product.price * i.quantity, 0),

      isInCart: (productId) => get().items.some((i) => i.product.id === productId),
    })),
    { name: 'smartshop-cart' },
  ),
);
