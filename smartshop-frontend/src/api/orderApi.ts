/**
 * orderApi.ts — Order service API (/orders)
 *
 * CONNECTED TO:
 * - CheckoutPage, OrdersPage, OrderDetailPage
 */

import { axiosInstance } from './axiosInstance';
import type { ApiResponse } from '@/types/api.types';
import type { CreateOrderRequest, Order } from '@/types/order.types';

export const orderApi = {
  async create(request: CreateOrderRequest): Promise<Order> {
    const { data } = await axiosInstance.post<ApiResponse<Order>>('/orders', request);
    return data.data;
  },

  async getById(id: string): Promise<Order> {
    const { data } = await axiosInstance.get<ApiResponse<Order>>(`/orders/${id}`);
    return data.data;
  },

  async getByUser(userId: string): Promise<Order[]> {
    const { data } = await axiosInstance.get<ApiResponse<Order[]>>(
      `/orders/user/${userId}`,
    );
    return data.data;
  },
};
