/**
 * Header.tsx — Top navigation, cart badge, user menu
 */

import { Link, NavLink, useLocation, useNavigate } from 'react-router-dom';
import { motion, useReducedMotion } from 'framer-motion';
import { Menu as MenuIcon, ShoppingCart, User } from 'lucide-react';
import { Menu, MenuButton, MenuItem, MenuItems } from '@headlessui/react';
import { useCart } from '@hooks/useCart';
import { useAuth } from '@hooks/useAuth';
import { useUiStore } from '@store/uiStore';
import { Badge } from '@components/ui/Badge';
import { ROUTES } from '@utils/constants';
import { canManageProducts, isSuperAdmin } from '@utils/roles';
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
    ...(isAuthenticated && canManageProducts(user?.role)
      ? [
          { to: ROUTES.VENDOR, label: 'Vendor', protected: true as const },
          { to: ROUTES.BRAND, label: 'My products', protected: true as const },
        ]
      : []),
    ...(isAuthenticated && isSuperAdmin(user?.role)
      ? [{ to: ROUTES.ADMIN, label: 'Admin', protected: true as const }]
      : []),
  ];

  return (
    <header className="sticky top-0 z-40 w-full border-b border-slate-700/50 glass-panel">
      <div className="flex h-16 w-full items-center justify-between gap-4 px-4 sm:px-6">
        <div className="flex items-center gap-3">
          <button
            type="button"
            className="icon-btn lg:hidden"
            onClick={toggleSidebar}
            aria-label="Open menu"
          >
            <MenuIcon className="h-6 w-6" />
          </button>
          <Link
            to={ROUTES.HOME}
            className="group flex items-center gap-2 font-display font-bold tracking-tight text-white transition-opacity hover:opacity-90"
          >
            <svg
              className="h-8 w-8 transition-transform duration-300 group-hover:scale-105 group-hover:rotate-3"
              viewBox="0 0 32 32"
              aria-hidden
            >
              <defs>
                <linearGradient id="logo-grad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stopColor="#8b5cf6" />
                  <stop offset="100%" stopColor="#22d3ee" />
                </linearGradient>
              </defs>
              <rect width="32" height="32" rx="8" fill="url(#logo-grad)" />
              <path d="M8 12h16l-2 14H10L8 12z" fill="#0c0e14" />
            </svg>
            <span className="hidden bg-gradient-accent bg-clip-text text-transparent sm:inline">
              SmartShop
            </span>
          </Link>
        </div>

        <nav className="hidden items-center gap-1 md:flex">
          {navLinks.map((link) => {
            if (link.protected && !isAuthenticated) return null;
            const isActive = location.pathname === link.to;
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
                {isActive && shouldReduceMotion && (
                  <span className="absolute inset-x-1 -bottom-0.5 h-0.5 rounded-full bg-gradient-accent" />
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
            <ShoppingCart className="h-6 w-6 transition-transform duration-200 group-hover:scale-110" />
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
              <MenuItems
                className={cn(
                  'absolute right-0 mt-2 w-48 origin-top-right rounded-xl border border-slate-700/60',
                  'glass-panel py-1 shadow-card-hover focus:outline-none animate-slide-down',
                )}
              >
                {canManageProducts(user?.role) && (
                  <MenuItem>
                    {({ focus }) => (
                      <button
                        type="button"
                        className={cn(
                          'block w-full px-4 py-2 text-left text-sm text-slate-200 transition-colors',
                          focus && 'bg-primary-600/20 text-white',
                        )}
                        onClick={() => navigate(ROUTES.BRAND)}
                      >
                        My products
                      </button>
                    )}
                  </MenuItem>
                )}
                {isSuperAdmin(user?.role) && (
                  <MenuItem>
                    {({ focus }) => (
                      <button
                        type="button"
                        className={cn(
                          'block w-full px-4 py-2 text-left text-sm text-slate-200 transition-colors',
                          focus && 'bg-primary-600/20 text-white',
                        )}
                        onClick={() => navigate(ROUTES.ADMIN)}
                      >
                        Admin dashboard
                      </button>
                    )}
                  </MenuItem>
                )}
                <MenuItem>
                  {({ focus }) => (
                    <button
                      type="button"
                      className={cn(
                        'block w-full px-4 py-2 text-left text-sm text-slate-200 transition-colors',
                        focus && 'bg-primary-600/20 text-white',
                      )}
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
                      className={cn(
                        'block w-full px-4 py-2 text-left text-sm text-slate-200 transition-colors',
                        focus && 'bg-primary-600/20 text-white',
                      )}
                      onClick={() => navigate(ROUTES.ORDERS)}
                    >
                      My Orders
                    </button>
                  )}
                </MenuItem>
                <MenuItem>
                  {({ focus }) => (
                    <button
                      type="button"
                      className={cn(
                        'block w-full px-4 py-2 text-left text-sm text-red-400 transition-colors',
                        focus && 'bg-red-950/50 text-red-300',
                      )}
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
              className="rounded-lg bg-gradient-accent px-4 py-2 text-sm font-medium text-white shadow-glow-sm transition-all duration-200 hover:scale-[1.02] hover:shadow-glow active:scale-[0.98]"
            >
              Sign in
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}
