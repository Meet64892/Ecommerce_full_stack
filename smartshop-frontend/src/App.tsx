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
const AdminDashboardHome = lazy(() => import('./pages/admin/AdminDashboardHome'));
const AdminBrandsPage = lazy(() => import('./pages/admin/AdminBrandsPage'));
const AdminProductsPage = lazy(() => import('./pages/admin/AdminProductsPage'));
const AdminUsersPage = lazy(() => import('./pages/admin/AdminUsersPage'));
const AdminOrdersPage = lazy(() => import('./pages/admin/AdminOrdersPage'));
const BrandProductsPage = lazy(() => import('./pages/BrandProductsPage'));
const VendorApplyPage = lazy(() => import('./pages/VendorApplyPage'));
const VendorDashboardPage = lazy(() => import('./pages/VendorDashboardPage'));
const VendorOrdersPage = lazy(() => import('./pages/VendorOrdersPage'));

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
          >
            <Route index element={<AdminDashboardHome />} />
            <Route path="brands" element={<AdminBrandsPage />} />
            <Route path="products" element={<AdminProductsPage />} />
            <Route path="users" element={<AdminUsersPage />} />
            <Route path="orders" element={<AdminOrdersPage />} />
          </Route>
          <Route
            path={ROUTES.VENDOR}
            element={
              <RoleProtectedRoute allowedRoles={['SUPER_USER', 'SUPER_ADMIN']}>
                <VendorDashboardPage />
              </RoleProtectedRoute>
            }
          />
          <Route
            path={ROUTES.VENDOR_APPLY}
            element={
              <ProtectedRoute>
                <VendorApplyPage />
          <Route
            path={ROUTES.VENDOR_ORDERS}
            element={
              <RoleProtectedRoute allowedRoles={['SUPER_USER', 'SUPER_ADMIN']}>
                <VendorOrdersPage />
              </RoleProtectedRoute>
            }
          />
              </ProtectedRoute>
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
