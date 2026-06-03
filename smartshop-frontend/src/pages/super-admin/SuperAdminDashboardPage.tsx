import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { brandApi } from '@api/brandApi';
import { productApi } from '@api/productApi';
import { Users, Store, Package, Clock } from 'lucide-react';

export default function SuperAdminDashboardPage() {
  const { data: stats } = useQuery({
    queryKey: ['platform-stats'],
    queryFn: () => brandApi.platformStats(),
  });

  const { data: pendingProducts } = useQuery({
    queryKey: ['products-pending-count'],
    queryFn: async () => {
      const page = await productApi.listPending(0, 1);
      return page.totalElements;
    },
  });

  const cards = [
    { label: 'Customers', value: stats?.totalCustomers ?? '—', icon: Users },
    { label: 'Active vendors', value: stats?.approvedBrands ?? '—', icon: Store },
    { label: 'Pending vendor apps', value: stats?.pendingBrandApplications ?? '—', icon: Clock },
    { label: 'Products awaiting review', value: pendingProducts ?? '—', icon: Package },
  ];

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
      <h1 className="font-display text-2xl font-bold gradient-text">Platform dashboard</h1>
      <p className="mt-1 text-sm text-slate-400">Marketplace overview and performance</p>
      <div className="mt-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {cards.map((c) => (
          <div key={c.label} className="surface-card p-5">
            <c.icon className="mb-3 h-6 w-6 text-primary-400" />
            <p className="text-2xl font-bold text-slate-100">{c.value}</p>
            <p className="mt-1 text-sm text-slate-400">{c.label}</p>
          </div>
        ))}
      </div>
      <p className="mt-8 text-sm text-slate-500">
        Commission reports, payouts, and CMS modules connect here as the platform grows.
      </p>
    </motion.div>
  );
}
