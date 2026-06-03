import { axiosInstance } from './axiosInstance';
import type { ApiResponse } from '@/types/api.types';

export type BrandStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED';

export interface Brand {
  id: string;
  name: string;
  description?: string;
  ownerUserId: string;
  status: BrandStatus;
  commissionPercent: number;
  createdAt: string;
}

export const brandApi = {
  async listApproved(): Promise<Brand[]> {
    const { data } = await axiosInstance.get<ApiResponse<Brand[]>>('/brands');
    return data.data;
  },

  async apply(payload: { brandName: string; description?: string }): Promise<Brand> {
    const { data } = await axiosInstance.post<ApiResponse<Brand>>('/brands/apply', payload);
    return data.data;
  },

  async myBrand(): Promise<Brand> {
    const { data } = await axiosInstance.get<ApiResponse<Brand>>('/brands/me');
    return data.data;
  },
};
