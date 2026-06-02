/**
 * authApi.ts — Authentication endpoints (/auth/*)
 *
 * PURPOSE:
 * Thin wrappers around axios — unwrap ApiResponse<T>.data for callers.
 *
 * CONNECTED TO:
 * - authStore, LoginForm, RegisterForm
 */

import { axiosInstance } from './axiosInstance';
import type { ApiResponse } from '@/types/api.types';
import type { AuthResponse, LoginRequest, RegisterRequest } from '@/types/auth.types';
import type { User } from '@/types/product.types';

export const authApi = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const { data } = await axiosInstance.post<ApiResponse<AuthResponse>>(
      '/auth/login',
      credentials,
    );
    return data.data;
  },

  async register(payload: RegisterRequest): Promise<AuthResponse> {
    const { data } = await axiosInstance.post<ApiResponse<AuthResponse>>(
      '/auth/register',
      payload,
    );
    return data.data;
  },

  async me(): Promise<User> {
    const { data } = await axiosInstance.get<ApiResponse<User>>('/auth/me');
    return data.data;
  },
};
