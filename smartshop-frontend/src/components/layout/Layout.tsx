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
 * Layout — Full-width themed shell with no horizontal padding on main content.
 */
export function Layout() {
  return (
    <div className="flex min-h-screen w-full flex-col bg-surface-muted">
      <Header />
      <div className="flex w-full flex-1">
        <Sidebar />
        <main className="full-bleed w-full flex-1 py-6">
          <Outlet />
        </main>
      </div>
      <Footer />
      <CartDrawer />
    </div>
  );
}
