import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { brandApi } from '@api/brandApi';
import { Button } from '@components/ui/Button';
import { Badge } from '@components/ui/Badge';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

export default function SuperAdminBrandsPage() {
  const queryClient = useQueryClient();
  const { data, isLoading } = useQuery({
    queryKey: ['brands-pending'],
    queryFn: () => brandApi.listPending(0, 50),
  });

  const approve = useMutation({
    mutationFn: (id: string) => brandApi.approve(id),
    onSuccess: () => {
      toast.success('Vendor approved');
      void queryClient.invalidateQueries({ queryKey: ['brands-pending'] });
      void queryClient.invalidateQueries({ queryKey: ['platform-stats'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  const reject = useMutation({
    mutationFn: (id: string) => brandApi.reject(id),
    onSuccess: () => {
      toast.success('Application rejected');
      void queryClient.invalidateQueries({ queryKey: ['brands-pending'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  const brands = data?.content ?? [];

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
      <h1 className="font-display text-2xl font-bold gradient-text">Brand / vendor management</h1>
      <p className="mt-1 text-sm text-slate-400">Review onboarding applications and manage vendors</p>
      {isLoading ? (
        <p className="mt-8 text-slate-500">Loading…</p>
      ) : brands.length === 0 ? (
        <p className="mt-8 text-slate-500">No pending vendor applications.</p>
      ) : (
        <ul className="mt-6 space-y-4">
          {brands.map((b) => (
            <li key={b.id} className="surface-card flex flex-col gap-4 p-5 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <div className="flex items-center gap-2">
                  <h2 className="font-semibold text-slate-100">{b.name}</h2>
                  <Badge>{b.status}</Badge>
                </div>
                <p className="mt-1 text-sm text-slate-400">/{b.slug}</p>
                <p className="mt-2 text-sm text-slate-300">{b.description}</p>
              </div>
              <div className="flex shrink-0 gap-2">
                <Button size="sm" isLoading={approve.isPending} onClick={() => approve.mutate(b.id)}>
                  Approve
                </Button>
                <Button size="sm" variant="secondary" isLoading={reject.isPending} onClick={() => reject.mutate(b.id)}>
                  Reject
                </Button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </motion.div>
  );
}
