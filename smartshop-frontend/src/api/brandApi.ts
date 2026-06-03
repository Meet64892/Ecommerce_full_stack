import { axiosInstance } from './axiosInstance';
import type { ApiResponse, PaginatedResponse } from '@/types/api.types';
import type { Brand, BrandApplyRequest, CreateVendorAdminRequest, PlatformStats } from '@/types/brand.types';

export const brandApi = {
  async apply(payload: BrandApplyRequest): Promise<Brand> {
    const { data } = await axiosInstance.post<ApiResponse<Brand>>('/brands/apply', payload);
    return data.data;
  },

  async getMyBrand(): Promise<Brand> {
    const { data } = await axiosInstance.get<ApiResponse<Brand>>('/brands/me');
    return data.data;
  },

  async getById(id: string): Promise<Brand> {
    const { data } = await axiosInstance.get<ApiResponse<Brand>>(`/brands/${id}`);
    return data.data;
  },

  async listPending(page = 0, size = 20): Promise<PaginatedResponse<Brand>> {
    const { data } = await axiosInstance.get<ApiResponse<PaginatedResponse<Brand>>>('/brands/pending', {
      params: { page, size },
    });
    return data.data;
  },

  async list(page = 0, size = 20): Promise<PaginatedResponse<Brand>> {
    const { data } = await axiosInstance.get<ApiResponse<PaginatedResponse<Brand>>>('/brands', {
      params: { page, size },
    });
    return data.data;
  },

  async approve(id: string, commissionPercent?: number): Promise<Brand> {
    const { data } = await axiosInstance.post<ApiResponse<Brand>>(`/brands/${id}/approve`, null, {
      params: commissionPercent != null ? { commissionPercent } : undefined,
    });
    return data.data;
  },

  async reject(id: string): Promise<Brand> {
    const { data } = await axiosInstance.post<ApiResponse<Brand>>(`/brands/${id}/reject`);
    return data.data;
  },

  async suspend(id: string): Promise<Brand> {
    const { data } = await axiosInstance.post<ApiResponse<Brand>>(`/brands/${id}/suspend`);
    return data.data;
  },

  async createVendorAdmin(payload: CreateVendorAdminRequest): Promise<Brand> {
    const { data } = await axiosInstance.post<ApiResponse<Brand>>('/brands/vendor-admin', payload);
    return data.data;
  },

  async platformStats(): Promise<PlatformStats> {
    const { data } = await axiosInstance.get<ApiResponse<PlatformStats>>('/brands/stats/overview');
    return data.data;
  },
};
