/**
 * App.tsx — Storefront, Super Admin, and Vendor Admin route areas
 */

import { lazy, Suspense } from 'react';
import { Navigate, Routes, Route } from 'react-router-dom';
import { Layout } from '@components/layout/Layout';
import { DashboardLayout } from '@components/layout/DashboardLayout';
import { ProtectedRoute } from '@components/auth/ProtectedRoute';
import { RoleProtectedRoute } from '@components/auth/RoleProtectedRoute';
import { Skeleton } from '@components/ui/Skeleton';
import { ROUTES, SUPER_ADMIN_ROUTES, VENDOR_ROUTES } from '@utils/constants';
import { SUPER_ADMIN_NAV, VENDOR_ADMIN_NAV } from '@/config/dashboardNav';

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
const BrandApplyPage = lazy(() => import('./pages/BrandApplyPage'));

const SuperAdminDashboardPage = lazy(() => import('./pages/super-admin/SuperAdminDashboardPage'));
const SuperAdminBrandsPage = lazy(() => import('./pages/super-admin/SuperAdminBrandsPage'));
const SuperAdminProductsPage = lazy(() => import('./pages/super-admin/SuperAdminProductsPage'));
const SuperAdminOrdersPage = lazy(() => import('./pages/super-admin/SuperAdminOrdersPage'));
const SuperAdminUsersPage = lazy(() => import('./pages/super-admin/SuperAdminUsersPage'));
const SuperAdminSettingsPage = lazy(() => import('./pages/super-admin/SuperAdminSettingsPage'));

const VendorDashboardPage = lazy(() => import('./pages/vendor/VendorDashboardPage'));
const VendorProductsPage = lazy(() => import('./pages/vendor/VendorProductsPage'));
const VendorOrdersPage = lazy(() => import('./pages/vendor/VendorOrdersPage'));
const VendorPromotionsPage = lazy(() => import('./pages/vendor/VendorPromotionsPage'));
const VendorReportsPage = lazy(() => import('./pages/vendor/VendorReportsPage'));

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
          <Route path={ROUTES.SELL} element={<BrandApplyPage />} />
          <Route path={ROUTES.LOGIN} element={<LoginPage />} />
          <Route path={ROUTES.REGISTER} element={<RegisterPage />} />

          <Route
            path="super-admin"
            element={
              <RoleProtectedRoute allowedRoles={['SUPER_ADMIN']}>
                <DashboardLayout title="Super Admin" navItems={SUPER_ADMIN_NAV} />
              </RoleProtectedRoute>
            }
          >
            <Route index element={<Navigate to={SUPER_ADMIN_ROUTES.DASHBOARD} replace />} />
            <Route path="dashboard" element={<SuperAdminDashboardPage />} />
            <Route path="brands" element={<SuperAdminBrandsPage />} />
            <Route path="products" element={<SuperAdminProductsPage />} />
            <Route path="orders" element={<SuperAdminOrdersPage />} />
            <Route path="users" element={<SuperAdminUsersPage />} />
            <Route path="settings" element={<SuperAdminSettingsPage />} />
          </Route>

          <Route
            path="admin"
            element={
              <RoleProtectedRoute allowedRoles={['ADMIN', 'SUPER_ADMIN']}>
                <DashboardLayout title="Brand admin" navItems={VENDOR_ADMIN_NAV} />
              </RoleProtectedRoute>
            }
          >
            <Route index element={<Navigate to={VENDOR_ROUTES.DASHBOARD} replace />} />
            <Route path="dashboard" element={<VendorDashboardPage />} />
            <Route path="products" element={<VendorProductsPage />} />
            <Route path="orders" element={<VendorOrdersPage />} />
            <Route path="promotions" element={<VendorPromotionsPage />} />
            <Route path="reports" element={<VendorReportsPage />} />
          </Route>

          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </Suspense>
  );
}
