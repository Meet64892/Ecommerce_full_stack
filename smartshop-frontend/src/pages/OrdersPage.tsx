/**
 * OrdersPage.tsx — Order history with client-side filter tabs
 */

import { useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { motion, AnimatePresence } from 'framer-motion';
import { orderApi } from '@api/orderApi';
import { useAuth } from '@hooks/useAuth';
import { OrderCard } from '@components/order/OrderCard';
import { Spinner } from '@components/ui/Spinner';
import type { OrderStatus } from '@/types/order.types';
import { Package } from 'lucide-react';

type Tab = 'ALL' | OrderStatus;

const TABS: { id: Tab; label: string }[] = [
  { id: 'ALL', label: 'All' },
  { id: 'PENDING', label: 'Pending' },
  { id: 'DELIVERED', label: 'Delivered' },
  { id: 'CANCELLED', label: 'Cancelled' },
];

export default function OrdersPage() {
  const { user } = useAuth();
  const [tab, setTab] = useState<Tab>('ALL');

  const { data: orders = [], isLoading } = useQuery({
    queryKey: ['orders', user?.id],
    queryFn: () => orderApi.getByUser(user!.id),
    enabled: !!user?.id,
    select: (data) => data,
  });

  const filtered = useMemo(() => {
    if (tab === 'ALL') return orders;
    return orders.filter((o) => o.status === tab);
  }, [orders, tab]);

  return (
    <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }}>
      <h1 className="mb-6 text-2xl font-bold">My orders</h1>

      <div className="mb-6 flex flex-wrap gap-2">
        {TABS.map((t) => (
          <button
            key={t.id}
            type="button"
            onClick={() => setTab(t.id)}
            className={`rounded-full px-4 py-1.5 text-sm font-medium ${
              tab === t.id ? 'bg-primary-600 text-white' : 'bg-gray-100 text-gray-700'
            }`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <Spinner size="lg" />
      ) : filtered.length === 0 ? (
        <div className="flex flex-col items-center py-16 text-gray-500">
          <Package className="mb-4 h-12 w-12" />
          <p>No orders in this view.</p>
        </div>
      ) : (
        <AnimatePresence>
          <motion.div layout className="space-y-4">
            {filtered.map((order) => (
              <motion.div key={order.id} layout initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
                <OrderCard order={order} />
              </motion.div>
            ))}
          </motion.div>
        </AnimatePresence>
      )}
    </motion.div>
  );
}
