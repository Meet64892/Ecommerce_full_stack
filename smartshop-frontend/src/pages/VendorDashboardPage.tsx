import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { Package, Store, Truck } from 'lucide-react';
import { brandApi } from '@api/brandApi';
import { useAuth } from '@hooks/useAuth';
import { ROUTES } from '@utils/constants';
import { ROLE_LABELS } from '@utils/roles';

export default function VendorDashboardPage() {
  const { user } = useAuth();
  const { data: brand, isError } = useQuery({
    queryKey: ['brand', 'me'],
    queryFn: () => brandApi.myBrand(),
    retry: false,
  });

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} className="mx-auto max-w-3xl">
      <h1 className="font-display text-2xl font-bold gradient-text">Vendor dashboard</h1>
      <p className="mt-1 text-sm text-slate-400">
        {user?.fullName} · {ROLE_LABELS.SUPER_USER}
      </p>

      {isError ? (
        <div className="surface-card mt-6 p-6">
          <p className="text-slate-300">You have not applied to sell yet.</p>
          <Link to={ROUTES.VENDOR_APPLY} className="mt-4 inline-block text-primary-300 hover:underline">
            Apply to become a vendor →
          </Link>
        </div>
      ) : brand ? (
        <div className="surface-card mt-6 p-6">
          <Store className="mb-2 h-6 w-6 text-primary-400" />
          <p className="font-semibold text-slate-100">{brand.name}</p>
          <p className="text-sm text-slate-400">Status: {brand.status}</p>
          {brand.status === 'APPROVED' && (
            <p className="mt-2 text-sm text-emerald-400">You can add products — they go to Super Admin for approval.</p>
          )}
          {brand.status === 'PENDING' && (
            <p className="mt-2 text-sm text-amber-300">Waiting for Super Admin to approve your brand.</p>
          )}
        </div>
      ) : null}

      <div className="mt-6 grid gap-4 sm:grid-cols-2">
        <Link to={ROUTES.BRAND} className="surface-card block p-5 hover:border-primary-500/40">
          <Package className="mb-2 h-6 w-6 text-accent-300" />
          <h2 className="font-semibold text-slate-100">Product management</h2>
          <p className="mt-1 text-sm text-slate-400">Add and manage your catalog</p>
        </Link>
        <Link to={ROUTES.VENDOR_ORDERS} className="surface-card block p-5 hover:border-primary-500/40">
          <Truck className="mb-2 h-6 w-6 text-primary-400" />
          <h2 className="font-semibold text-slate-100">Orders</h2>
          <p className="mt-1 text-sm text-slate-400">Fulfill orders for your brand</p>
        </Link>
        <div className="surface-card p-5 opacity-80">
          <Truck className="mb-2 h-6 w-6 text-slate-500" />
          <h2 className="font-semibold text-slate-100">Orders</h2>
          <p className="mt-1 text-sm text-slate-500">Process brand orders (API wiring next)</p>
        </div>
      </div>
    </motion.div>
  );
}
