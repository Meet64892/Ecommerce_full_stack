/**
 * main.tsx — Application entry point
 *
 * PURPOSE:
 * Mounts React 18 root and wraps the tree with global providers (React Query, Router, Toasts).
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - createRoot: React 18 concurrent root API (replaces ReactDOM.render).
 * - StrictMode: Double-invokes effects in dev to surface impure side effects.
 * - Provider order: QueryClientProvider must wrap components that call useQuery.
 * - ReactQueryDevtools: Inspect cache, stale state, manual invalidation (dev only).
 *
 * CONNECTED TO:
 * - App.tsx
 */

import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { Toaster } from 'react-hot-toast';
import App from './App';
import './styles/globals.css';
import './styles/animations.css';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // Data is "fresh" for 5 minutes — no background refetch during this window
      staleTime: 1000 * 60 * 5,
      // Unused cache entries are garbage-collected after 10 minutes (formerly cacheTime)
      gcTime: 1000 * 60 * 10,
      retry: 2,
      // When user returns to the tab, refetch stale queries (fresh stock/prices)
      refetchOnWindowFocus: true,
    },
  },
});

const rootElement = document.getElementById('root');
if (!rootElement) {
  throw new Error('Root element #root not found');
}

createRoot(rootElement).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
      {import.meta.env.VITE_ENABLE_DEVTOOLS === 'true' && (
        <ReactQueryDevtools initialIsOpen={false} />
      )}
      <Toaster
        position="top-right"
        toastOptions={{
          style: {
            background: '#1a1f2e',
            color: '#f1f5f9',
            border: '1px solid rgba(139, 92, 246, 0.35)',
            boxShadow: '0 0 20px -4px rgba(139, 92, 246, 0.35)',
          },
        }}
      />
    </QueryClientProvider>
  </StrictMode>,
);
