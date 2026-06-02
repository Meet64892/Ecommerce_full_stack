/**
 * uiStore.ts — Ephemeral UI state (theme, sidebar, toasts queue metadata)
 *
 * PURPOSE:
 * UI chrome that is not server data — kept separate from auth/cart for clarity.
 *
 * CONNECTED TO:
 * - Header, Sidebar, Layout
 */

import { create } from 'zustand';
import { persist } from 'zustand/middleware';

type Theme = 'light' | 'dark';

interface UiState {
  theme: Theme;
  sidebarOpen: boolean;
  cartDrawerOpen: boolean;
  setTheme: (theme: Theme) => void;
  toggleSidebar: () => void;
  setSidebarOpen: (open: boolean) => void;
  setCartDrawerOpen: (open: boolean) => void;
}

export const useUiStore = create<UiState>()(
  persist(
    (set) => ({
      theme: 'light',
      sidebarOpen: false,
      cartDrawerOpen: false,
      setTheme: (theme) => set({ theme }),
      toggleSidebar: () => set((s) => ({ sidebarOpen: !s.sidebarOpen })),
      setSidebarOpen: (sidebarOpen) => set({ sidebarOpen }),
      setCartDrawerOpen: (cartDrawerOpen) => set({ cartDrawerOpen }),
    }),
    {
      name: 'smartshop-ui',
      partialize: (state) => ({ theme: state.theme }),
    },
  ),
);
