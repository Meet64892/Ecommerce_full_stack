/**
 * useAuth.ts — Ergonomic facade over authStore + React Query
 *
 * PURPOSE:
 * Components import one hook instead of reaching into Zustand + API separately.
 *
 * CONNECTED TO:
 * - ProtectedRoute, Header, LoginPage
 */

import { useCallback } from 'react';
import { useAuthStore } from '@store/authStore';
import type { LoginRequest, RegisterRequest } from '@/types/auth.types';
import { authApi } from '@api/authApi';

export function useAuth() {
  const user = useAuthStore((s) => s.user);
  const token = useAuthStore((s) => s.token);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const isLoading = useAuthStore((s) => s.isLoading);
  const login = useAuthStore((s) => s.login);
  const logout = useAuthStore((s) => s.logout);
  const setSession = useAuthStore((s) => s.setSession);
  const refreshUser = useAuthStore((s) => s.refreshUser);

  const register = useCallback(
    async (payload: RegisterRequest) => {
      const auth = await authApi.register(payload);
      setSession(auth.token, auth.user);
      return auth;
    },
    [setSession],
  );

  return {
    user,
    token,
    isAuthenticated,
    isLoading,
    login: (credentials: LoginRequest) => login(credentials),
    register,
    logout,
    refreshUser,
  };
}
