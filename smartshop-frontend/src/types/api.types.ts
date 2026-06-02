/**
 * api.types.ts — Shared API envelope types mirroring the Spring backend
 *
 * PURPOSE:
 * TypeScript interfaces that match backend DTOs give compile-time safety and IDE autocomplete.
 * Generic wrappers like ApiResponse<T> describe the same JSON shape every microservice returns.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Generics (<T>): One interface describes many response payloads while keeping `data` typed.
 * - PaginatedResponse: Maps to Spring Data Page<T> (content, totalElements, 0-based page number).
 * - Record<string, string>: Field-level validation errors keyed by form field name.
 *
 * CONNECTED TO:
 * - src/api/*.ts (unwrap ApiResponse.data)
 * - src/utils/errorHandler.ts (ErrorResponse parsing)
 */

/** Mirrors com.smartshop.common.dto.ApiResponse — success envelope with typed payload */
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
  timestamp: string;
}

/**
 * Mirrors Spring Data Page<T> serialized to JSON.
 * `number` is the current page index (0-based in Spring, same as page query param).
 */
export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/** Mirrors com.smartshop.common.dto.ErrorResponse for global exception handling */
export interface ErrorResponse {
  code: string;
  message: string;
  details: Record<string, string>;
  timestamp: string;
}

/**
 * Discriminated union — TypeScript narrows `data` only when status is 'success'.
 * Useful for local UI state machines (not always needed when using React Query).
 */
export type AsyncState<T> =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'success'; data: T }
  | { status: 'error'; error: string };

/** Type guard — narrows unknown catch errors to our API error shape */
export function isApiError(error: unknown): error is ErrorResponse {
  return typeof error === 'object' && error !== null && 'code' in error && 'message' in error;
}
