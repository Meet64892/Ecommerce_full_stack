/**
 * App.tsx — React Router configuration with lazy-loaded pages
 *
 * PURPOSE:
 * Defines URL → page mapping, nested layout routes, auth guards, and code splitting.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Nested routes: Layout renders <Outlet /> for child routes.
 * - React.lazy + Suspense: Dynamic import splits bundle per route.
 * - ProtectedRoute: Centralized auth redirect with location.state.from.
 * - BrowserRouter vs HashRouter: Clean URLs (/products) require server fallback in production.
 *
 * CONNECTED TO:
 * - main.tsx, pages/*, components/layout/Layout.tsx
 */

import { lazy, Suspense } from 'react';
import { Routes, Route } from 'react-router-dom';
import { Layout } from '@components/layout/Layout';
import { ProtectedRoute } from '@components/auth/ProtectedRoute';
import { RoleProtectedRoute } from '@components/auth/RoleProtectedRoute';
import { Skeleton } from '@components/ui/Skeleton';
import { ROUTES } from '@utils/constants';

const HomePage = lazy(() => import('./pages/HomePage'));
const ProductListPage = lazy(() => import('./pages/ProductListPage'));
const ProductDetailPage = lazy(() => import('./pages/ProductDetailPage'));
const CartPage = lazy(() => import('./pages/CartPage'));
const CheckoutPage = lazy(() => import('./pages/CheckoutPage'));
const OrdersPage = lazy(() => import('./pages/OrdersPage'));
const OrderDetailPage = lazy(() => import('./pages/OrderDetailPage'));
const ProfilePage = lazy(() => import('./pages/ProfilePage'));
const LoginPage = lazy(() => import('./pages/LoginPage'));
const RegisterPage = lazy(() => import('./pages/RegisterPage'));
const NotFoundPage = lazy(() => import('./pages/NotFoundPage'));
const AdminDashboardPage = lazy(() => import('./pages/AdminDashboardPage'));
const BrandProductsPage = lazy(() => import('./pages/BrandProductsPage'));

function PageFallback() {
  return (
    <div className="space-y-4 p-4">
      <Skeleton className="h-8 w-1/3" />
      <Skeleton className="h-48 w-full" />
      <Skeleton.Text lines={4} />
    </div>
  );
}

export default function App() {
  return (
    <Suspense fallback={<PageFallback />}>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<HomePage />} />
          <Route path={ROUTES.PRODUCTS} element={<ProductListPage />} />
          <Route path="/products/:id" element={<ProductDetailPage />} />
          <Route path={ROUTES.CART} element={<CartPage />} />
          <Route
            path={ROUTES.CHECKOUT}
            element={
              <ProtectedRoute>
                <CheckoutPage />
              </ProtectedRoute>
            }
          />
          <Route
            path={ROUTES.ORDERS}
            element={
              <ProtectedRoute>
                <OrdersPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/orders/:id"
            element={
              <ProtectedRoute>
                <OrderDetailPage />
              </ProtectedRoute>
            }
          />
          <Route
            path={ROUTES.PROFILE}
            element={
              <ProtectedRoute>
                <ProfilePage />
              </ProtectedRoute>
            }
          />
          <Route
            path={ROUTES.ADMIN}
            element={
              <RoleProtectedRoute allowedRoles={['SUPER_ADMIN']}>
                <AdminDashboardPage />
              </RoleProtectedRoute>
            }
          />
          <Route
            path={ROUTES.BRAND}
            element={
              <RoleProtectedRoute allowedRoles={['SUPER_USER', 'SUPER_ADMIN']}>
                <BrandProductsPage />
              </RoleProtectedRoute>
            }
          />
          <Route path={ROUTES.LOGIN} element={<LoginPage />} />
          <Route path={ROUTES.REGISTER} element={<RegisterPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </Suspense>
  );
}
