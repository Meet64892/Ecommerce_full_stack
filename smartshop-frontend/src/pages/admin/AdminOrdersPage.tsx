import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '@api/adminApi';
import { formatCurrency } from '@utils/formatters';
import { ORDER_STATUS, ORDER_STATUS_LABELS } from '@utils/constants';
import type { OrderStatus } from '@/types/order.types';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

export default function AdminOrdersPage() {
  const queryClient = useQueryClient();
  const { data, isLoading } = useQuery({
    queryKey: ['admin', 'orders'],
    queryFn: () => adminApi.listOrders(0, 50),
  });

  const updateStatus = useMutation({
    mutationFn: ({ id, status }: { id: string; status: OrderStatus }) => adminApi.updateOrderStatus(id, status),
    onSuccess: () => {
      toast.success('Order status updated');
      void queryClient.invalidateQueries({ queryKey: ['admin', 'orders'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  if (isLoading) return <p className="text-slate-400">Loading orders…</p>;

  const orders = data?.content ?? [];

  return (
    <div>
      <h1 className="mb-4 font-display text-xl font-bold text-slate-100">Platform orders</h1>
      {orders.length === 0 ? (
        <p className="text-slate-400">No orders yet.</p>
      ) : (
        <div className="overflow-x-auto rounded-xl border border-slate-700/50">
          <table className="min-w-full text-left text-sm">
            <thead className="bg-slate-800/80 text-slate-300">
              <tr>
                <th className="px-4 py-3">Order</th>
                <th className="px-4 py-3">Customer</th>
                <th className="px-4 py-3">Total</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">Update</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((o) => (
                <tr key={o.id} className="border-t border-slate-700/40">
                  <td className="px-4 py-3 font-mono text-xs text-slate-400">{o.id.slice(0, 8)}…</td>
                  <td className="px-4 py-3 font-mono text-xs text-slate-400">{o.userId.slice(0, 8)}…</td>
                  <td className="px-4 py-3 text-slate-100">{formatCurrency(o.totalAmount)}</td>
                  <td className="px-4 py-3 text-slate-300">{ORDER_STATUS_LABELS[o.status]}</td>
                  <td className="px-4 py-3">
                    <select
                      className="rounded-lg border border-slate-600 bg-slate-800 px-2 py-1.5 text-slate-100"
                      value={o.status}
                      disabled={updateStatus.isPending}
                      onChange={(e) => {
                        const status = e.target.value as OrderStatus;
                        if (status !== o.status) updateStatus.mutate({ id: o.id, status });
                      }}
                    >
                      {ORDER_STATUS.map((s) => (
                        <option key={s} value={s}>
                          {ORDER_STATUS_LABELS[s]}
                        </option>
                      ))}
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
