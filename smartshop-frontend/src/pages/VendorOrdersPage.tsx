import { motion } from 'framer-motion';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { vendorApi } from '@api/vendorApi';
import { formatCurrency } from '@utils/formatters';
import { ORDER_STATUS_LABELS } from '@utils/constants';
import type { OrderStatus } from '@/types/order.types';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

const VENDOR_STATUSES: OrderStatus[] = ['CONFIRMED', 'SHIPPED', 'DELIVERED'];

export default function VendorOrdersPage() {
  const queryClient = useQueryClient();
  const { data, isLoading, isError } = useQuery({
    queryKey: ['vendor', 'orders'],
    queryFn: () => vendorApi.listOrders(0, 50),
    retry: false,
  });

  const updateStatus = useMutation({
    mutationFn: ({ id, status }: { id: string; status: OrderStatus }) =>
      vendorApi.updateOrderStatus(id, status),
    onSuccess: () => {
      toast.success('Fulfillment status updated');
      void queryClient.invalidateQueries({ queryKey: ['vendor', 'orders'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  if (isLoading) return <p className="text-slate-400">Loading your orders…</p>;
  if (isError) {
    return (
      <p className="text-amber-300">
        Orders are available after your brand is approved and linked to your account.
      </p>
    );
  }

  const orders = data?.content ?? [];

  return (
    <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} className="mx-auto max-w-4xl">
      <h1 className="font-display text-2xl font-bold text-slate-100">Your brand orders</h1>
      <p className="mt-1 text-sm text-slate-400">Only line items for your brand are shown.</p>

      {orders.length === 0 ? (
        <p className="mt-6 text-slate-400">No orders containing your products yet.</p>
      ) : (
        <ul className="mt-6 space-y-4">
          {orders.map((o) => (
            <li key={o.id} className="surface-card p-4">
              <div className="flex flex-wrap items-center justify-between gap-2">
                <div>
                  <p className="font-mono text-xs text-slate-500">#{o.id.slice(0, 8)}</p>
                  <p className="text-lg font-semibold text-slate-100">{formatCurrency(o.totalAmount)}</p>
                  <p className="text-sm text-slate-400">Status: {ORDER_STATUS_LABELS[o.status]}</p>
                </div>
                <select
                  className="rounded-lg border border-slate-600 bg-slate-800 px-2 py-1.5 text-slate-100"
                  value={o.status}
                  disabled={updateStatus.isPending || o.status === 'PENDING' || o.status === 'CANCELLED'}
                  onChange={(e) => {
                    const status = e.target.value as OrderStatus;
                    if (status !== o.status) updateStatus.mutate({ id: o.id, status });
                  }}
                >
                  <option value={o.status}>{ORDER_STATUS_LABELS[o.status]}</option>
                  {VENDOR_STATUSES.filter((s) => s !== o.status).map((s) => (
                    <option key={s} value={s}>
                      Mark {ORDER_STATUS_LABELS[s]}
                    </option>
                  ))}
                </select>
              </div>
              <ul className="mt-3 space-y-1 border-t border-slate-700/40 pt-3 text-sm text-slate-300">
                {o.items.map((item) => (
                  <li key={item.id}>
                    {item.productName ?? item.productId.slice(0, 8)} × {item.quantity} —{' '}
                    {formatCurrency(item.unitPrice * item.quantity)}
                  </li>
                ))}
              </ul>
            </li>
          ))}
        </ul>
      )}
    </motion.div>
  );
}
