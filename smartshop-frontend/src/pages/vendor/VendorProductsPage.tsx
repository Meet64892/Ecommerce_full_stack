import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { productApi } from '@api/productApi';
import { brandApi } from '@api/brandApi';
import { Input } from '@components/ui/Input';
import { Button } from '@components/ui/Button';
import { Select } from '@components/ui/Select';
import { Badge } from '@components/ui/Badge';
import { formatCurrency } from '@utils/formatters';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

const schema = z.object({
  name: z.string().min(2),
  description: z.string().min(10),
  price: z.coerce.number().min(0.01),
  stockKeepingUnit: z.string().regex(/^[A-Z0-9-]+$/),
  categoryId: z.string().uuid(),
});

type Form = z.infer<typeof schema>;

export default function VendorProductsPage() {
  const queryClient = useQueryClient();
  const { data: brand } = useQuery({ queryKey: ['my-brand'], queryFn: () => brandApi.getMyBrand() });
  const { data: categories = [] } = useQuery({
    queryKey: ['categories'],
    queryFn: () => productApi.getCategories(),
  });
  const { data: products } = useQuery({
    queryKey: ['vendor-products', brand?.id],
    queryFn: () => productApi.listByBrand(brand!.id, 0, 50),
    enabled: !!brand?.id,
  });

  const { register, handleSubmit, setValue, watch, reset, formState: { errors } } = useForm<Form>({
    resolver: zodResolver(schema),
  });

  const create = useMutation({
    mutationFn: (data: Form) =>
      productApi.create({ ...data, brandId: brand!.id }),
    onSuccess: () => {
      toast.success('Product submitted for review');
      reset();
      void queryClient.invalidateQueries({ queryKey: ['vendor-products'] });
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  if (brand?.status !== 'APPROVED') {
    return (
      <p className="text-slate-400">
        Your brand must be approved by the platform before you can add products.
      </p>
    );
  }

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} className="space-y-8">
      <div>
        <h1 className="font-display text-2xl font-bold gradient-text">Your products</h1>
        <p className="text-sm text-slate-400">New products require super admin approval before going live</p>
      </div>
      <form onSubmit={handleSubmit((d) => create.mutate(d))} className="surface-card max-w-lg space-y-4 p-6">
        <h2 className="font-semibold text-slate-100">Add product</h2>
        <Input label="Name" error={errors.name?.message} {...register('name')} />
        <div>
          <label className="mb-1 block text-sm text-slate-300">Description</label>
          <textarea {...register('description')} rows={3} className="w-full rounded-lg border border-neutral-700 bg-neutral-900 px-3 py-2 text-sm text-white" />
          {errors.description && <p className="mt-1 text-sm text-red-400">{errors.description.message}</p>}
        </div>
        <Input label="Price" type="number" step="0.01" error={errors.price?.message} {...register('price')} />
        <Input label="SKU" error={errors.stockKeepingUnit?.message} {...register('stockKeepingUnit')} />
        <Select
          label="Category"
          value={watch('categoryId') ?? ''}
          onChange={(v) => setValue('categoryId', v, { shouldValidate: true })}
          options={[{ value: '', label: 'Select…' }, ...categories.map((c) => ({ value: c.id, label: c.name }))]}
        />
        <Button type="submit" isLoading={create.isPending}>Submit for approval</Button>
      </form>
      <ul className="space-y-2">
        {(products?.content ?? []).map((p) => (
          <li key={p.id} className="surface-card flex justify-between p-4">
            <span className="text-slate-200">{p.name}</span>
            <span className="flex items-center gap-2">
              <Badge>{p.approvalStatus ?? 'PENDING'}</Badge>
              <span className="text-sm text-slate-400">{formatCurrency(p.price)}</span>
            </span>
          </li>
        ))}
      </ul>
    </motion.div>
  );
}
