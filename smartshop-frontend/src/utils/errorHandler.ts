/**
 * errorHandler.ts — Converts Axios/network errors into user-facing messages
 *
 * PURPOSE:
 * Backend returns ErrorResponse JSON; this module normalizes unknown errors for toasts and forms.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Async errors (failed fetch) are NOT caught by React Error Boundaries — handle in Query/mutations.
 * - Error Boundaries catch render-time errors in child components.
 *
 * CONNECTED TO:
 * - src/api/axiosInstance.ts
 * - React Query onError callbacks, toast notifications
 */

import axios, { type AxiosError } from 'axios';
import type { ErrorResponse } from '@/types/api.types';
import { isApiError } from '@/types/api.types';

/**
 * parseApiError — Extracts a display message from an Axios failure
 *
 * LEARNING NOTE: `error.response?.data` is typed as unknown until narrowed.
 *
 * @param error - Caught value from try/catch or React Query
 */
export function parseApiError(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<ErrorResponse>;
    const data = axiosError.response?.data;
    if (data && isApiError(data)) {
      const fieldErrors = Object.values(data.details ?? {});
      if (fieldErrors.length > 0) {
        return fieldErrors.join('. ');
      }
      return data.message;
    }
    if (axiosError.response?.status === 401) {
      return 'Your session expired. Please sign in again.';
    }
    if (axiosError.response?.status === 403) {
      return 'You do not have permission to perform this action.';
    }
    if (axiosError.message === 'Network Error') {
      return 'Unable to reach the server. Check that the API gateway is running on port 8080.';
    }
    return axiosError.message;
  }
  if (error instanceof Error) {
    return error.message;
  }
  return 'Something went wrong. Please try again.';
}
