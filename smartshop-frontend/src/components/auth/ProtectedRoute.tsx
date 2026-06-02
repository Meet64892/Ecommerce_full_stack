/**
 * ProtectedRoute.tsx — Redirects unauthenticated users to /login
 *
 * LEARNING NOTE: Preserves attempted URL in location.state for post-login redirect.
 */

import type { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '@hooks/useAuth';
import { Spinner } from '@components/ui/Spinner';
import { ROUTES } from '@utils/constants';

export function ProtectedRoute({ children }: { children: ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="flex min-h-[40vh] items-center justify-center">
        <Spinner size="lg" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to={ROUTES.LOGIN} replace state={{ from: location }} />;
  }

  return <>{children}</>;
}
