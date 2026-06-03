/**
 * product.types.ts — Catalog domain types
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
  brandId?: string;
  approvalStatus?: string;
}

export interface Category {
  id: string;
  name: string;
  description?: string;
}

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

export type ProductPatchRequest = Partial<Pick<Product, 'name' | 'price' | 'description'>>;

/** Mirrors com.smartshop.user.entity.Role */
export type UserRole = 'USER' | 'ADMIN' | 'SUPER_ADMIN';

/** Mirrors com.smartshop.user.dto.UserDto */
export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  role: UserRole;
  brandId?: string;
  enabled?: boolean;
  createdAt: string;
}
