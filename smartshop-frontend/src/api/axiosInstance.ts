/**
 * axiosInstance.ts — Configured HTTP client with interceptors
 *
 * PURPOSE:
 * Single axios instance shared by all API modules — consistent base URL, headers, and error handling.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Interceptors: Middleware pipeline that runs before every request / after every response.
 * - Custom instance vs axios.get: Encapsulates SmartShop defaults so api modules stay thin.
 * - import.meta.env: Vite injects VITE_* variables at build time (not Node's process.env).
 * - 401 vs 403: 401 = not authenticated → logout; 403 = authenticated but forbidden (no logout).
 * - JWT Bearer: `Authorization: Bearer <token>` is the standard header for stateless auth.
 *
 * CONNECTED TO:
 * - src/store/authStore.ts
 * - src/api/*.ts
 */

import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '@store/authStore';
import type { ErrorResponse } from '@/types/api.types';
import { parseApiError } from '@utils/errorHandler';

const baseURL = import.meta.env.VITE_API_BASE_URL ?? '/api';

export const axiosInstance = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

axiosInstance.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const { token } = useAuthStore.getState();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  if (import.meta.env.DEV) {
    (config as InternalAxiosRequestConfig & { metadata?: { start: number } }).metadata = {
      start: performance.now(),
    };
  }

  return config;
});

axiosInstance.interceptors.response.use(
  (response) => {
    if (import.meta.env.DEV) {
      const config = response.config as InternalAxiosRequestConfig & {
        metadata?: { start: number };
      };
      if (config.metadata?.start) {
        const ms = Math.round(performance.now() - config.metadata.start);
        console.debug(`[API] ${config.method?.toUpperCase()} ${config.url} — ${ms}ms`);
      }
    }
    return response;
  },
  (error: AxiosError<ErrorResponse>) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().logout();
      if (window.location.pathname !== '/login') {
        window.location.assign(
          `/login?redirect=${encodeURIComponent(window.location.pathname)}`,
        );
      }
    }

    const message = parseApiError(error);
    return Promise.reject(new Error(message));
  },
);
