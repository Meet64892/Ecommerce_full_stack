import { axiosInstance } from './axiosInstance';
import type { ApiResponse, PaginatedResponse } from '@/types/api.types';
import type { Order, OrderStatus } from '@/types/order.types';

export const vendorApi = {
  async listOrders(page = 0, size = 20): Promise<PaginatedResponse<Order>> {
    const { data } = await axiosInstance.get<ApiResponse<PaginatedResponse<Order>>>('/vendor/orders', {
      params: { page, size },
    });
    return data.data;
  },

  async updateOrderStatus(id: string, status: OrderStatus): Promise<Order> {
    const { data } = await axiosInstance.patch<ApiResponse<Order>>(`/vendor/orders/${id}/status`, {
      status,
    });
    return data.data;
  },
};
