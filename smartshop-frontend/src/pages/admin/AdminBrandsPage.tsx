import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '@api/adminApi';
import { Button } from '@components/ui/Button';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

export default function AdminBrandsPage() {
  const queryClient = useQueryClient();
  const { data, isLoading } = useQuery({
    queryKey: ['admin', 'brands', 'PENDING'],
    queryFn: () => adminApi.listBrands('PENDING', 0, 50),
  });

  const approve = useMutation({
    mutationFn: (id: string) => adminApi.approveBrand(id),
    onSuccess: () => {
      toast.success('Brand approved — vendor can list products');
      void queryClient.invalidateQueries({ queryKey: ['admin'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  const reject = useMutation({
    mutationFn: (id: string) => adminApi.rejectBrand(id),
    onSuccess: () => {
      toast.success('Brand application rejected');
      void queryClient.invalidateQueries({ queryKey: ['admin'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  if (isLoading) return <p className="text-slate-400">Loading applications…</p>;

  const brands = data?.content ?? [];

  return (
    <div>
      <h1 className="mb-4 font-display text-xl font-bold text-slate-100">Brand applications</h1>
      {brands.length === 0 ? (
        <p className="text-slate-400">No pending vendor applications.</p>
      ) : (
        <ul className="space-y-3">
          {brands.map((b) => (
            <li key={b.id} className="surface-card flex flex-wrap items-center justify-between gap-3 p-4">
              <div>
                <p className="font-semibold text-slate-100">{b.name}</p>
                <p className="text-sm text-slate-400">{b.description || 'No description'}</p>
              </div>
              <div className="flex gap-2">
                <Button size="sm" onClick={() => approve.mutate(b.id)} isLoading={approve.isPending}>
                  Approve
                </Button>
                <Button size="sm" variant="secondary" onClick={() => reject.mutate(b.id)}>
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
