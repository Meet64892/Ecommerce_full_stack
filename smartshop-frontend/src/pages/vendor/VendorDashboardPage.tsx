import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { brandApi } from '@api/brandApi';
import { productApi } from '@api/productApi';
import { useAuth } from '@hooks/useAuth';
import { Package, Store } from 'lucide-react';

export default function VendorDashboardPage() {
  const { user } = useAuth();
  const { data: brand } = useQuery({
    queryKey: ['my-brand'],
    queryFn: () => brandApi.getMyBrand(),
    enabled: !!user?.brandId || user?.role === 'ADMIN',
  });

  const { data: products } = useQuery({
    queryKey: ['vendor-products', brand?.id],
    queryFn: () => productApi.listByBrand(brand!.id, 0, 1),
    enabled: !!brand?.id,
  });

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
      <h1 className="font-display text-2xl font-bold gradient-text">Brand dashboard</h1>
      <p className="mt-1 text-sm text-slate-400">{brand?.name ?? 'Your brand'}</p>
      <div className="mt-8 grid gap-4 sm:grid-cols-2">
        <div className="surface-card p-5">
          <Store className="mb-2 h-6 w-6 text-primary-400" />
          <p className="text-lg font-semibold text-slate-100">{brand?.status ?? '—'}</p>
          <p className="text-sm text-slate-400">Brand status</p>
        </div>
        <div className="surface-card p-5">
          <Package className="mb-2 h-6 w-6 text-primary-400" />
          <p className="text-lg font-semibold text-slate-100">{products?.totalElements ?? 0}</p>
          <p className="text-sm text-slate-400">Products in catalog</p>
        </div>
      </div>
    </motion.div>
  );
}
