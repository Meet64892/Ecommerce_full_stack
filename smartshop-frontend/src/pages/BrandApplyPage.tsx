import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { brandApi } from '@api/brandApi';
import { Input } from '@components/ui/Input';
import { Button } from '@components/ui/Button';
import { ProtectedRoute } from '@components/auth/ProtectedRoute';
import { ROUTES } from '@utils/constants';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

const schema = z.object({
  name: z.string().min(2),
  description: z.string().min(20),
  slug: z.string().regex(/^[a-z0-9]+(?:-[a-z0-9]+)*$/, 'Use lowercase letters, numbers, and hyphens'),
});

type Form = z.infer<typeof schema>;

function BrandApplyForm() {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors } } = useForm<Form>({ resolver: zodResolver(schema) });

  const apply = useMutation({
    mutationFn: brandApi.apply,
    onSuccess: () => {
      toast.success('Application submitted! We will review your brand.');
      navigate(ROUTES.PROFILE);
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  return (
    <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="mx-auto max-w-lg">
      <h1 className="font-display text-2xl font-bold gradient-text">Sell on SmartShop</h1>
      <p className="mt-2 text-slate-400">
        Apply to become a brand owner. A super admin will review your application before you can list products.
      </p>
      <form onSubmit={handleSubmit((d) => apply.mutate(d))} className="surface-card mt-8 space-y-4 p-6">
        <Input label="Brand name" error={errors.name?.message} {...register('name')} />
        <Input label="Store URL slug" placeholder="my-brand" error={errors.slug?.message} {...register('slug')} />
        <div>
          <label className="mb-1 block text-sm text-slate-300">About your brand</label>
          <textarea {...register('description')} rows={4} className="w-full rounded-lg border border-neutral-700 bg-neutral-900 px-3 py-2 text-sm text-white" />
          {errors.description && <p className="mt-1 text-sm text-red-400">{errors.description.message}</p>}
        </div>
        <Button type="submit" isLoading={apply.isPending} className="w-full">
          Submit application
        </Button>
      </form>
    </motion.div>
  );
}

export default function BrandApplyPage() {
  return (
    <ProtectedRoute>
      <BrandApplyForm />
    </ProtectedRoute>
  );
}
