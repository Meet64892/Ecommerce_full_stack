/**
 * userApi.ts — User profile API (/users)
 *
 * CONNECTED TO:
 * - ProfilePage
 */

import { axiosInstance } from './axiosInstance';
import type { ApiResponse } from '@/types/api.types';
import type { User } from '@/types/product.types';

export interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
}

export const userApi = {
  async getById(id: string): Promise<User> {
    const { data } = await axiosInstance.get<ApiResponse<User>>(`/users/${id}`);
    return data.data;
  },

  async update(id: string, payload: UpdateUserRequest): Promise<User> {
    const { data } = await axiosInstance.put<ApiResponse<User>>(`/users/${id}`, payload);
    return data.data;
  },
};
