import { Link, Outlet, useLocation } from 'react-router-dom';
import { cn } from '@utils/cn';

export interface DashboardNavItem {
  to: string;
  label: string;
}

export function DashboardLayout({ title, items }: { title: string; items: DashboardNavItem[] }) {
  const location = useLocation();

  return (
    <div className="mx-auto flex max-w-6xl flex-col gap-6 lg:flex-row">
      <aside className="surface-card w-full shrink-0 p-4 lg:w-56">
        <h2 className="mb-4 font-display text-lg font-semibold text-slate-100">{title}</h2>
        <nav className="flex flex-col gap-1">
          {items.map((item) => (
            <Link
              key={item.to}
              to={item.to}
              className={cn(
                'rounded-lg px-3 py-2 text-sm transition-colors',
                location.pathname === item.to
                  ? 'bg-primary-500/20 text-primary-200'
                  : 'text-slate-400 hover:bg-slate-800 hover:text-slate-100',
              )}
            >
              {item.label}
            </Link>
          ))}
        </nav>
      </aside>
      <div className="min-w-0 flex-1">
        <Outlet />
      </div>
    </div>
  );
}
