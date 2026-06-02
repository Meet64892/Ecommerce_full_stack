/**
 * inventoryApi.ts — Stock levels from inventory-service (/inventory)
 *
 * CONNECTED TO:
 * - ProductDetailPage quantity selector
 */

import { axiosInstance } from './axiosInstance';
import type { ApiResponse } from '@/types/api.types';

export interface Inventory {
  productId: string;
  quantityAvailable: number;
  quantityReserved: number;
  salableQuantity: number;
  version: number;
}

export const inventoryApi = {
  async getByProductId(productId: string): Promise<Inventory> {
    const { data } = await axiosInstance.get<ApiResponse<Inventory>>(
      `/inventory/${productId}`,
    );
    return data.data;
  },
};
