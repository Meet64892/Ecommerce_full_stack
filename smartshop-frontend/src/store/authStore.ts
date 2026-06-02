/**
 * authStore.ts — Global authentication state (Zustand + persist)
 *
 * PURPOSE:
 * Holds JWT and current user so any component and axios interceptors can read auth without prop drilling.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Zustand vs Redux: Minimal boilerplate, hook-based API, no Provider wrapper required.
 * - persist middleware: Serializes slice to localStorage so refresh keeps the session.
 * - isAuthenticated flag: Explicit boolean vs `user !== null` — documents intent for route guards.
 * - Why not Context for auth: Context re-renders all consumers when any field changes.
 *
 * CONNECTED TO:
 * - src/api/axiosInstance.ts (reads token for Authorization header)
 * - src/hooks/useAuth.ts
 * - ProtectedRoute, LoginPage
 */

import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { LoginRequest } from '@/types/auth.types';
import type { User } from '@/types/product.types';
import { authApi } from '@api/authApi';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => void;
  refreshUser: () => Promise<void>;
  setSession: (token: string, user: User) => void;
}

/**
 * useAuthStore — Zustand store factory with persistence
 *
 * LEARNING NOTE: `getState()` is used outside React (e.g. axios interceptors) without subscribing.
 */
export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,

      setSession: (token, user) => {
        set({ token, user, isAuthenticated: true, isLoading: false });
      },

      login: async (credentials) => {
        set({ isLoading: true });
        try {
          const auth = await authApi.login(credentials);
          set({
            token: auth.token,
            user: auth.user,
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error) {
          set({ isLoading: false });
          throw error;
        }
      },

      logout: () => {
        set({ user: null, token: null, isAuthenticated: false, isLoading: false });
      },

      refreshUser: async () => {
        const { token } = get();
        if (!token) return;
        try {
          const user = await authApi.me();
          set({ user, isAuthenticated: true });
        } catch {
          get().logout();
        }
      },
    }),
    {
      name: 'smartshop-auth',
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated,
      }),
    },
  ),
);
