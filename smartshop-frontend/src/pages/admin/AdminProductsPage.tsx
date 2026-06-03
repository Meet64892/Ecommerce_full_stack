import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '@api/adminApi';
import { Button } from '@components/ui/Button';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

export default function AdminProductsPage() {
  const queryClient = useQueryClient();
  const { data, isLoading } = useQuery({
    queryKey: ['admin', 'products', 'pending'],
    queryFn: () => adminApi.listPendingProducts(0, 50),
  });

  const approve = useMutation({
    mutationFn: (id: string) => adminApi.approveProduct(id),
    onSuccess: () => {
      toast.success('Product published to marketplace');
      void queryClient.invalidateQueries({ queryKey: ['admin', 'products'] });
      void queryClient.invalidateQueries({ queryKey: ['products'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  const reject = useMutation({
    mutationFn: (id: string) => adminApi.rejectProduct(id),
    onSuccess: () => {
      toast.success('Product rejected');
      void queryClient.invalidateQueries({ queryKey: ['admin', 'products'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  if (isLoading) return <p className="text-slate-400">Loading pending products…</p>;

  const products = data?.content ?? [];

  return (
    <div>
      <h1 className="mb-4 font-display text-xl font-bold text-slate-100">Product moderation</h1>
      {products.length === 0 ? (
        <p className="text-slate-400">No products awaiting approval.</p>
      ) : (
        <ul className="space-y-3">
          {products.map((p) => (
            <li key={p.id} className="surface-card flex flex-wrap items-center justify-between gap-3 p-4">
              <div>
                <p className="font-semibold text-slate-100">{p.name}</p>
                <p className="text-sm text-slate-400">
                  ₹{p.price} · {p.approvalStatus ?? 'PENDING'}
                </p>
              </div>
              <div className="flex gap-2">
                <Button size="sm" onClick={() => approve.mutate(p.id)}>
                  Approve
                </Button>
                <Button size="sm" variant="secondary" onClick={() => reject.mutate(p.id)}>
                  Reject
                </Button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
