import { axiosInstance } from './axiosInstance';
import type { ApiResponse, PaginatedResponse } from '@/types/api.types';
import type { Brand } from './brandApi';
import type { Product, User, UserRole } from '@/types/product.types';

export interface PlatformStats {
  totalUsers: number;
  totalCustomers: number;
  totalBrandOwners: number;
  pendingBrandApplications: number;
  approvedBrands: number;
}

export const adminApi = {
  async stats(): Promise<PlatformStats> {
    const { data } = await axiosInstance.get<ApiResponse<PlatformStats>>('/admin/stats');
    return data.data;
  },

  async listBrands(status = 'PENDING', page = 0, size = 20): Promise<PaginatedResponse<Brand>> {
    const { data } = await axiosInstance.get<ApiResponse<PaginatedResponse<Brand>>>('/admin/brands', {
      params: { status, page, size },
    });
    return data.data;
  },

  async approveBrand(id: string): Promise<Brand> {
    const { data } = await axiosInstance.patch<ApiResponse<Brand>>(`/admin/brands/${id}/approve`);
    return data.data;
  },

  async rejectBrand(id: string): Promise<Brand> {
    const { data } = await axiosInstance.patch<ApiResponse<Brand>>(`/admin/brands/${id}/reject`);
    return data.data;
  },

  async listUsers(page = 0, size = 20): Promise<PaginatedResponse<User>> {
    const { data } = await axiosInstance.get<ApiResponse<PaginatedResponse<User>>>('/admin/users', {
      params: { page, size },
    });
    return data.data;
  },

  async listPendingProducts(page = 0, size = 20): Promise<PaginatedResponse<Product>> {
    const { data } = await axiosInstance.get<ApiResponse<PaginatedResponse<Product>>>(
      '/admin/products/pending',
      { params: { page, size } },
    );
    return data.data;
  },

  async approveProduct(id: string): Promise<Product> {
    const { data } = await axiosInstance.patch<ApiResponse<Product>>(`/admin/products/${id}/approve`);
    return data.data;
  },

  async rejectProduct(id: string): Promise<Product> {
    const { data } = await axiosInstance.patch<ApiResponse<Product>>(`/admin/products/${id}/reject`);
    return data.data;
  },

  async updateUserRole(userId: string, role: UserRole): Promise<User> {
    const { data } = await axiosInstance.patch<ApiResponse<User>>(`/admin/users/${userId}/role`, { role });
    return data.data;
  },
};
