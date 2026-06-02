/**
 * cart.types.ts — Client-side shopping cart types
 *
 * PURPOSE:
 * The cart lives in Zustand + localStorage (not the backend until checkout).
 * CartItem extends Product with quantity for line-item display.
 *
 * CONNECTED TO:
 * - src/store/cartStore.ts
 */

import type { Product } from './product.types';

export interface CartItem {
  product: Product;
  quantity: number;
}
