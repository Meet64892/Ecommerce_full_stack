/**
 * auth.types.ts — Authentication and user identity types
 *
 * PURPOSE:
 * Aligns with user-service DTOs (LoginRequest, AuthResponse, UserDto).
 *
 * CONNECTED TO:
 * - src/store/authStore.ts
 * - src/api/authApi.ts
 */

import type { User } from './product.types';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresInSeconds: number;
  user: User;
}

export type { User };
