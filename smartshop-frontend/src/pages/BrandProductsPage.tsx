/**
 * BrandProductsPage.tsx — Super user (brand owner) product management
 */

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { Store } from 'lucide-react';
import { productApi } from '@api/productApi';
import { Input } from '@components/ui/Input';
import { Button } from '@components/ui/Button';
import { Select } from '@components/ui/Select';
import { useAuth } from '@hooks/useAuth';
import { ROLE_LABELS } from '@utils/roles';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

const productSchema = z.object({
  name: z.string().min(2, 'Name is required'),
  description: z.string().min(10, 'Description must be at least 10 characters'),
  price: z.coerce.number().min(0.01, 'Price must be greater than 0'),
  stockKeepingUnit: z
    .string()
    .regex(/^[A-Z0-9-]+$/, 'SKU must be uppercase letters, numbers, or hyphens'),
  categoryId: z.string().uuid('Select a category'),
});

type ProductForm = z.infer<typeof productSchema>;

export default function BrandProductsPage() {
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const { data: categories = [] } = useQuery({
    queryKey: ['categories'],
    queryFn: () => productApi.getCategories(),
  });

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<ProductForm>({
    resolver: zodResolver(productSchema),
    defaultValues: { price: 0, stockKeepingUnit: '', categoryId: '' },
  });

  const createMutation = useMutation({
    mutationFn: productApi.create,
    onSuccess: () => {
      toast.success('Product submitted — pending Super Admin approval before it appears in the shop');
      reset();
      void queryClient.invalidateQueries({ queryKey: ['products'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  const categoryId = watch('categoryId');

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      className="mx-auto max-w-lg"
    >
      <div className="mb-6 flex items-center gap-3">
        <Store className="h-8 w-8 text-primary-400" />
        <div>
          <h1 className="font-display text-2xl font-bold gradient-text">Your products</h1>
          <p className="text-sm text-slate-400">
            {user?.fullName} · {ROLE_LABELS.SUPER_USER}
          </p>
        </div>
      </div>

      <form
        onSubmit={handleSubmit((data) => createMutation.mutate(data))}
        className="surface-card space-y-4 p-6"
      >
        <h2 className="text-lg font-semibold text-slate-100">Add a product</h2>
        <Input label="Product name" error={errors.name?.message} {...register('name')} />
        <div>
          <label className="mb-1 block text-sm font-medium text-slate-300">Description</label>
          <textarea
            {...register('description')}
            rows={3}
            className="w-full rounded-lg border border-neutral-700 bg-neutral-900 px-3 py-2 text-sm text-white focus:border-white focus:outline-none focus:ring-2 focus:ring-white/20"
          />
          {errors.description && (
            <p className="mt-1 text-sm text-red-400">{errors.description.message}</p>
          )}
        </div>
        <Input
          label="Price (₹)"
          type="number"
          step="0.01"
          error={errors.price?.message}
          {...register('price')}
        />
        <Input
          label="SKU"
          placeholder="e.g. BRAND-001"
          error={errors.stockKeepingUnit?.message}
          {...register('stockKeepingUnit')}
        />
        <div>
          <Select
            label="Category"
            value={categoryId}
            onChange={(v) => setValue('categoryId', v, { shouldValidate: true })}
            options={[
              { value: '', label: 'Select category…' },
              ...categories.map((c) => ({ value: c.id, label: c.name })),
            ]}
          />
          {errors.categoryId && (
            <p className="mt-1 text-sm text-red-400">{errors.categoryId.message}</p>
          )}
        </div>
        <Button type="submit" isLoading={createMutation.isPending} className="w-full">
          Add product
        </Button>
      </form>
    </motion.div>
  );
}
