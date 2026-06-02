/**
 * productApi.ts — Product catalog API (/products, /categories)
 *
 * PURPOSE:
 * Encapsulates catalog reads/search. `params` become query string; `data` is JSON body (POST/PUT).
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Unwrapping ApiResponse.data inside the module so components receive plain Product | Page.
 * - Spring Pageable: page (0-indexed), size, sort query params.
 *
 * CONNECTED TO:
 * - ProductListPage (useInfiniteQuery), ProductDetailPage (useQuery)
 */

import { axiosInstance } from './axiosInstance';
import type { ApiResponse, PaginatedResponse } from '@/types/api.types';
import type {
  Category,
  Product,
  ProductFilters,
  ProductSearchParams,
} from '@/types/product.types';
import { PAGE_SIZE } from '@utils/constants';

function toSpringPage(
  raw: PaginatedResponse<Product>,
): PaginatedResponse<Product> {
  return raw;
}

export const productApi = {
  async getAll(params: ProductSearchParams = {}): Promise<PaginatedResponse<Product>> {
    const { data } = await axiosInstance.get<ApiResponse<PaginatedResponse<Product>>>(
      '/products',
      {
        params: {
          page: params.page ?? 0,
          size: params.size ?? PAGE_SIZE,
          sort: params.sort,
        },
      },
    );
    return toSpringPage(data.data);
  },

  async getById(id: string): Promise<Product> {
    const { data } = await axiosInstance.get<ApiResponse<Product>>(`/products/${id}`);
    return data.data;
  },

  async search(
    query: string,
    filters: ProductFilters = {},
    page = 0,
    size = PAGE_SIZE,
    sort?: string,
  ): Promise<PaginatedResponse<Product>> {
    const { data } = await axiosInstance.get<ApiResponse<PaginatedResponse<Product>>>(
      '/products/search',
      {
        params: {
          query: query || undefined,
          categoryId: filters.categoryId,
          minPrice: filters.minPrice,
          maxPrice: filters.maxPrice,
          minRating: filters.minRating,
          page,
          size,
          sort,
        },
      },
    );
    return data.data;
  },

  async getByCategory(
    categoryId: string,
    page = 0,
    size = PAGE_SIZE,
  ): Promise<PaginatedResponse<Product>> {
    return productApi.search('', { categoryId }, page, size);
  },

  async getFeatured(): Promise<Product[]> {
    const page = await productApi.getAll({ page: 0, size: 8, sort: 'rating,desc' });
    return page.content;
  },

  async getCategories(): Promise<Category[]> {
    const { data } = await axiosInstance.get<ApiResponse<Category[]>>('/categories');
    return data.data;
  },
};
