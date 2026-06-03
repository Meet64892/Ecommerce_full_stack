/**
 * DashboardLayout.tsx — Sidebar layout for Super Admin and Vendor Admin panels
 */

import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '@hooks/useAuth';
import { ROUTES } from '@utils/constants';
import { ROLE_LABELS } from '@utils/roles';
import type { NavItem } from '@/config/dashboardNav';
import { cn } from '@utils/cn';

interface DashboardLayoutProps {
  title: string;
  navItems: NavItem[];
}

export function DashboardLayout({ title, navItems }: DashboardLayoutProps) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="flex min-h-[calc(100vh-4rem)]">
      <aside className="glass-panel hidden w-64 shrink-0 flex-col border-r border-slate-700/50 p-4 md:flex">
        <div className="mb-6 border-b border-slate-700/50 pb-4">
          <p className="text-xs font-medium uppercase tracking-wide text-slate-500">{title}</p>
          <p className="mt-1 truncate text-sm font-semibold text-slate-100">{user?.fullName}</p>
          {user?.role && <p className="text-xs text-primary-400">{ROLE_LABELS[user.role]}</p>}
        </div>
        <nav className="flex flex-1 flex-col gap-1">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                cn(
                  'flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors',
                  isActive
                    ? 'bg-primary-600/20 text-white'
                    : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200',
                )
              }
            >
              <item.icon className="h-4 w-4 shrink-0" />
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="mt-4 space-y-2 border-t border-slate-700/50 pt-4">
          <Link to={ROUTES.HOME} className="block text-sm text-slate-400 hover:text-white">
            ← Back to store
          </Link>
          <button
            type="button"
            onClick={() => {
              logout();
              navigate(ROUTES.LOGIN);
            }}
            className="text-sm text-red-400 hover:text-red-300"
          >
            Logout
          </button>
        </div>
      </aside>
      <main className="flex-1 overflow-auto p-4 md:p-8">
        <Outlet />
      </main>
    </div>
  );
}
