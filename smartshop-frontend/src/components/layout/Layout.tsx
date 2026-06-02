/**
 * Layout.tsx — App shell with header, footer, outlet for nested routes
 *
 * PURPOSE:
 * React Router v6 nested routes render child routes in <Outlet />.
 *
 * CONNECTED TO:
 * - App.tsx routes
 */

import { Outlet } from 'react-router-dom';
import { Header } from './Header';
import { Footer } from './Footer';
import { Sidebar } from './Sidebar';
import { CartDrawer } from '@components/cart/CartDrawer';

/**
 * Layout — Persistent chrome wrapping page content
 *
 * LEARNING NOTE: Mobile-first — single column by default; md: breakpoints widen layout.
 */
export function Layout() {
  return (
    <div className="flex min-h-screen flex-col bg-gray-50">
      <Header />
      <div className="flex flex-1">
        <Sidebar />
        <main className="mx-auto w-full max-w-7xl flex-1 px-4 py-6 sm:px-6 lg:px-8">
          <Outlet />
        </main>
      </div>
      <Footer />
      <CartDrawer />
    </div>
  );
}
