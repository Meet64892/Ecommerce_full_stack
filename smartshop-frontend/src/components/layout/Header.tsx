/**
 * Header.tsx — Top navigation with role-aware links
 */

import { Link, NavLink, useLocation, useNavigate } from 'react-router-dom';
import { motion, useReducedMotion } from 'framer-motion';
import { Menu as MenuIcon, ShoppingCart, User } from 'lucide-react';
import { Menu, MenuButton, MenuItem, MenuItems } from '@headlessui/react';
import { useCart } from '@hooks/useCart';
import { useAuth } from '@hooks/useAuth';
import { useUiStore } from '@store/uiStore';
import { Badge } from '@components/ui/Badge';
import { ROUTES, SUPER_ADMIN_ROUTES, VENDOR_ROUTES } from '@utils/constants';
import {
  canAccessSuperAdminPanel,
  canAccessVendorPanel,
  isCustomer,
} from '@utils/roles';
import { cn } from '@utils/cn';

export function Header() {
  const { totalItems } = useCart();
  const { isAuthenticated, user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const setCartDrawerOpen = useUiStore((s) => s.setCartDrawerOpen);
  const toggleSidebar = useUiStore((s) => s.toggleSidebar);
  const shouldReduceMotion = useReducedMotion();

  const navLinks = [
    { to: ROUTES.HOME, label: 'Home' },
    { to: ROUTES.PRODUCTS, label: 'Shop' },
    { to: ROUTES.ORDERS, label: 'Orders', protected: true },
    ...(isAuthenticated && canAccessSuperAdminPanel(user?.role)
      ? [{ to: SUPER_ADMIN_ROUTES.DASHBOARD, label: 'Super Admin', protected: true as const }]
      : []),
    ...(isAuthenticated && canAccessVendorPanel(user?.role)
      ? [{ to: VENDOR_ROUTES.DASHBOARD, label: 'Brand panel', protected: true as const }]
      : []),
  ];

  return (
    <header className="sticky top-0 z-40 w-full border-b border-slate-700/50 glass-panel">
      <div className="flex h-16 w-full items-center justify-between gap-4 px-4 sm:px-6">
        <div className="flex items-center gap-3">
          <button type="button" className="icon-btn lg:hidden" onClick={toggleSidebar} aria-label="Open menu">
            <MenuIcon className="h-6 w-6" />
          </button>
          <Link
            to={ROUTES.HOME}
            className="group flex items-center gap-2 font-display font-bold tracking-tight text-white transition-opacity hover:opacity-90"
          >
            <span className="hidden bg-gradient-accent bg-clip-text text-transparent sm:inline">SmartShop</span>
          </Link>
        </div>

        <nav className="hidden items-center gap-1 md:flex">
          {navLinks.map((link) => {
            if (link.protected && !isAuthenticated) return null;
            const isActive = location.pathname.startsWith(link.to);
            return (
              <NavLink key={link.to} to={link.to} className="nav-link">
                {link.label}
                {isActive && !shouldReduceMotion && (
                  <motion.span
                    layoutId="nav-underline"
                    className="absolute inset-x-1 -bottom-0.5 h-0.5 rounded-full bg-gradient-accent shadow-glow-sm"
                    transition={{ type: 'spring', stiffness: 380, damping: 30 }}
                  />
                )}
              </NavLink>
            );
          })}
        </nav>

        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => setCartDrawerOpen(true)}
            className="icon-btn group relative"
            aria-label="Open cart"
          >
            <ShoppingCart className="h-6 w-6" />
            {totalItems > 0 && (
              <span className="absolute -right-0.5 -top-0.5 cart-bounce">
                <Badge>{totalItems}</Badge>
              </span>
            )}
          </button>

          {isAuthenticated ? (
            <Menu as="div" className="relative">
              <MenuButton className="icon-btn flex items-center gap-2">
                <User className="h-5 w-5" />
                <span className="hidden text-sm font-medium text-slate-100 sm:inline">{user?.firstName}</span>
              </MenuButton>
              <MenuItems className="glass-panel absolute right-0 mt-2 w-52 origin-top-right rounded-xl border border-slate-700/60 py-1 shadow-card-hover animate-slide-down">
                {canAccessSuperAdminPanel(user?.role) && (
                  <MenuItem>
                    {({ focus }) => (
                      <button
                        type="button"
                        className={cn('block w-full px-4 py-2 text-left text-sm', focus && 'bg-primary-600/20 text-white')}
                        onClick={() => navigate(SUPER_ADMIN_ROUTES.DASHBOARD)}
                      >
                        Super Admin
                      </button>
                    )}
                  </MenuItem>
                )}
                {canAccessVendorPanel(user?.role) && (
                  <MenuItem>
                    {({ focus }) => (
                      <button
                        type="button"
                        className={cn('block w-full px-4 py-2 text-left text-sm', focus && 'bg-primary-600/20 text-white')}
                        onClick={() => navigate(VENDOR_ROUTES.DASHBOARD)}
                      >
                        Brand panel
                      </button>
                    )}
                  </MenuItem>
                )}
                {isCustomer(user?.role) && (
                  <MenuItem>
                    {({ focus }) => (
                      <button
                        type="button"
                        className={cn('block w-full px-4 py-2 text-left text-sm', focus && 'bg-primary-600/20 text-white')}
                        onClick={() => navigate(ROUTES.SELL)}
                      >
                        Sell on SmartShop
                      </button>
                    )}
                  </MenuItem>
                )}
                <MenuItem>
                  {({ focus }) => (
                    <button
                      type="button"
                      className={cn('block w-full px-4 py-2 text-left text-sm', focus && 'bg-primary-600/20 text-white')}
                      onClick={() => navigate(ROUTES.PROFILE)}
                    >
                      My Profile
                    </button>
                  )}
                </MenuItem>
                <MenuItem>
                  {({ focus }) => (
                    <button
                      type="button"
                      className={cn('block w-full px-4 py-2 text-left text-sm text-red-400', focus && 'bg-red-950/50')}
                      onClick={() => {
                        logout();
                        navigate(ROUTES.LOGIN);
                      }}
                    >
                      Logout
                    </button>
                  )}
                </MenuItem>
              </MenuItems>
            </Menu>
          ) : (
            <Link
              to={ROUTES.LOGIN}
              className="rounded-lg bg-gradient-accent px-4 py-2 text-sm font-medium text-white shadow-glow-sm"
            >
              Sign in
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}
