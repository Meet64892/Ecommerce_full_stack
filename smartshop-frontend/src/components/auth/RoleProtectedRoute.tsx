/**
 * RoleProtectedRoute.tsx — Restricts routes to users with allowed roles
 */

import type { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '@hooks/useAuth';
import { Spinner } from '@components/ui/Spinner';
import { ROUTES } from '@utils/constants';
import type { UserRole } from '@/types/product.types';
import { hasAnyRole } from '@utils/roles';

interface RoleProtectedRouteProps {
  children: ReactNode;
  allowedRoles: UserRole[];
}

export function RoleProtectedRoute({ children, allowedRoles }: RoleProtectedRouteProps) {
  const { user, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="flex min-h-[40vh] items-center justify-center">
        <Spinner size="lg" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to={ROUTES.LOGIN} replace />;
  }

  if (!hasAnyRole(user?.role, allowedRoles)) {
    return <Navigate to={ROUTES.HOME} replace />;
  }

  return <>{children}</>;
}
