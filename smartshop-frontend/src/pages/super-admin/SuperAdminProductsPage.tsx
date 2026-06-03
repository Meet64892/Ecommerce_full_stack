import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { productApi } from '@api/productApi';
import { Button } from '@components/ui/Button';
import { Badge } from '@components/ui/Badge';
import { formatCurrency } from '@utils/formatters';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

export default function SuperAdminProductsPage() {
  const queryClient = useQueryClient();
  const { data, isLoading } = useQuery({
    queryKey: ['products-pending'],
    queryFn: () => productApi.listPending(0, 50),
  });

  const approve = useMutation({
    mutationFn: productApi.approve,
    onSuccess: () => {
      toast.success('Product published');
      void queryClient.invalidateQueries({ queryKey: ['products-pending'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  const reject = useMutation({
    mutationFn: productApi.reject,
    onSuccess: () => {
      toast.success('Product rejected');
      void queryClient.invalidateQueries({ queryKey: ['products-pending'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  const products = data?.content ?? [];

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
      <h1 className="font-display text-2xl font-bold gradient-text">Product moderation</h1>
      <p className="mt-1 text-sm text-slate-400">Approve vendor products before they appear in the shop</p>
      {isLoading ? (
        <p className="mt-8 text-slate-500">Loading…</p>
      ) : products.length === 0 ? (
        <p className="mt-8 text-slate-500">No products awaiting approval.</p>
      ) : (
        <ul className="mt-6 space-y-3">
          {products.map((p) => (
            <li key={p.id} className="surface-card flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <div className="flex items-center gap-2">
                  <span className="font-medium text-slate-100">{p.name}</span>
                  <Badge>{p.approvalStatus ?? 'PENDING'}</Badge>
                </div>
                <p className="text-sm text-slate-400">{formatCurrency(p.price)} · {p.categoryName}</p>
              </div>
              <div className="flex gap-2">
                <Button size="sm" onClick={() => approve.mutate(p.id)}>Approve</Button>
                <Button size="sm" variant="secondary" onClick={() => reject.mutate(p.id)}>Reject</Button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </motion.div>
  );
}
