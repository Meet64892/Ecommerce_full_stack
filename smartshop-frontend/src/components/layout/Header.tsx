/**
 * Header.tsx — Top navigation, cart badge, user menu
 *
 * CONNECTED TO:
 * - cartStore, useAuth, useLocation, @headlessui/react Menu
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
import { cn } from '@utils/cn';

const navLinks = [
  { to: ROUTES.HOME, label: 'Home' },
  { to: ROUTES.PRODUCTS, label: 'Shop' },
  { to: ROUTES.ORDERS, label: 'Orders', protected: true },
];

export function Header() {
  const { totalItems } = useCart();
  const { isAuthenticated, user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const setCartDrawerOpen = useUiStore((s) => s.setCartDrawerOpen);
  const toggleSidebar = useUiStore((s) => s.toggleSidebar);
  const shouldReduceMotion = useReducedMotion();

  return (
    <header className="sticky top-0 z-40 border-b border-gray-200 bg-white/95 backdrop-blur">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between gap-4 px-4 sm:px-6 lg:px-8">
        <div className="flex items-center gap-3">
          <button
            type="button"
            className="rounded-lg p-2 text-gray-600 hover:bg-gray-100 lg:hidden"
            onClick={toggleSidebar}
            aria-label="Open menu"
          >
            <MenuIcon className="h-6 w-6" />
          </button>
          <Link to={ROUTES.HOME} className="flex items-center gap-2 font-bold text-primary-600">
            <svg className="h-8 w-8" viewBox="0 0 32 32" aria-hidden>
              <rect width="32" height="32" rx="8" fill="#2563eb" />
              <path d="M8 12h16l-2 14H10L8 12z" fill="#fff" />
            </svg>
            <span className="hidden sm:inline">SmartShop</span>
          </Link>
        </div>

        <nav className="hidden items-center gap-1 md:flex">
          {navLinks.map((link) => {
            if (link.protected && !isAuthenticated) return null;
            const isActive = location.pathname === link.to;
            return (
              <NavLink
                key={link.to}
                to={link.to}
                className="relative px-3 py-2 text-sm font-medium text-gray-600 hover:text-primary-600"
              >
                {link.label}
                {isActive && !shouldReduceMotion && (
                  <motion.span
                    layoutId="nav-underline"
                    className="absolute inset-x-1 -bottom-0.5 h-0.5 rounded bg-primary-600"
                  />
                )}
                {isActive && shouldReduceMotion && (
                  <span className="absolute inset-x-1 -bottom-0.5 h-0.5 rounded bg-primary-600" />
                )}
              </NavLink>
            );
          })}
        </nav>

        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => setCartDrawerOpen(true)}
            className="relative rounded-lg p-2 text-gray-600 hover:bg-gray-100"
            aria-label="Open cart"
          >
            <ShoppingCart className="h-6 w-6" />
            {totalItems > 0 && (
              <span className="absolute -right-0.5 -top-0.5">
                <Badge>{totalItems}</Badge>
              </span>
            )}
          </button>

          {isAuthenticated ? (
            <Menu as="div" className="relative">
              <MenuButton className="flex items-center gap-2 rounded-lg p-2 hover:bg-gray-100">
                <User className="h-5 w-5 text-gray-600" />
                <span className="hidden text-sm font-medium sm:inline">{user?.firstName}</span>
              </MenuButton>
              <MenuItems
                className={cn(
                  'absolute right-0 mt-2 w-48 origin-top-right rounded-lg border border-gray-100',
                  'bg-white py-1 shadow-lg focus:outline-none',
                )}
              >
                <MenuItem>
                  {({ focus }) => (
                    <button
                      type="button"
                      className={cn('block w-full px-4 py-2 text-left text-sm', focus && 'bg-gray-50')}
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
                      className={cn('block w-full px-4 py-2 text-left text-sm', focus && 'bg-gray-50')}
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
                      className={cn('block w-full px-4 py-2 text-left text-sm text-red-600', focus && 'bg-red-50')}
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
              className="rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white hover:bg-primary-700"
            >
              Sign in
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}
