/**
 * product.types.ts — Catalog domain types
 *
 * PURPOSE:
 * Mirrors ProductDto and search parameters from product-service.
 *
 * CONNECTED TO:
 * - src/api/productApi.ts
 * - src/store/cartStore.ts
 */

/** Mirrors com.smartshop.product.dto.ProductDto */
export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  stockKeepingUnit: string;
  rating: number;
  categoryId: string;
  categoryName: string;
}

export interface Category {
  id: string;
  name: string;
  description?: string;
}

/** Query params for GET /products/search — bound as axios `params` (query string) */
export interface ProductSearchParams {
  query?: string;
  categoryId?: string;
  minPrice?: number;
  maxPrice?: number;
  minRating?: number;
  page?: number;
  size?: number;
  sort?: string;
}

export interface ProductFilters {
  categoryId?: string;
  minPrice?: number;
  maxPrice?: number;
  minRating?: number;
}

/** Utility type — partial patch for admin updates (demonstrates Pick + Partial) */
export type ProductPatchRequest = Partial<Pick<Product, 'name' | 'price' | 'description'>>;

/** Mirrors com.smartshop.user.entity.Role */
export type UserRole = 'USER' | 'SUPER_USER' | 'SUPER_ADMIN';

/** Mirrors com.smartshop.user.dto.UserDto */
export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  role: UserRole;
  createdAt: string;
}
