import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { BarChart3, Package, Store, Users } from 'lucide-react';
import { adminApi } from '@api/adminApi';
import { useAuth } from '@hooks/useAuth';
import { ROLE_LABELS } from '@utils/roles';

export default function AdminDashboardHome() {
  const { user } = useAuth();
  const { data: stats } = useQuery({
    queryKey: ['admin', 'stats'],
    queryFn: () => adminApi.stats(),
  });

  const cards = [
    { label: 'Total users', value: stats?.totalUsers ?? '—', icon: Users },
    { label: 'Customers', value: stats?.totalCustomers ?? '—', icon: Users },
    { label: 'Vendors', value: stats?.totalBrandOwners ?? '—', icon: Store },
    { label: 'Pending brands', value: stats?.pendingBrandApplications ?? '—', icon: Store },
    { label: 'Approved brands', value: stats?.approvedBrands ?? '—', icon: Store },
    { label: 'Commission (demo)', value: '10%', icon: BarChart3 },
  ];

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
      <p className="mb-6 text-sm text-slate-400">
        {user?.fullName} · {ROLE_LABELS.SUPER_ADMIN} — platform-wide control
      </p>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {cards.map(({ label, value, icon: Icon }) => (
          <div key={label} className="surface-card p-5">
            <Icon className="mb-2 h-5 w-5 text-accent-300" />
            <p className="text-2xl font-bold text-slate-100">{value}</p>
            <p className="text-sm text-slate-400">{label}</p>
          </div>
        ))}
      </div>
      <div className="surface-card mt-6 p-6">
        <Package className="mb-2 h-6 w-6 text-primary-400" />
        <h3 className="font-semibold text-slate-100">Super Admin capabilities</h3>
        <p className="mt-2 text-sm text-slate-400">
          Approve vendor brands, moderate product listings, manage users, and view marketplace analytics.
          Use the sidebar to open brand applications, product review, and user management.
        </p>
      </div>
    </motion.div>
  );
}
