import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation } from '@tanstack/react-query';
import { Link, useNavigate } from 'react-router-dom';
import { brandApi } from '@api/brandApi';
import { Input } from '@components/ui/Input';
import { Button } from '@components/ui/Button';
import { ROUTES } from '@utils/constants';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

const schema = z.object({
  brandName: z.string().min(2, 'Brand name is required'),
  description: z.string().optional(),
});

type Form = z.infer<typeof schema>;

export default function VendorApplyPage() {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors } } = useForm<Form>({
    resolver: zodResolver(schema),
  });

  const mutation = useMutation({
    mutationFn: brandApi.apply,
    onSuccess: () => {
      toast.success('Application submitted. Super Admin will review your brand.');
      navigate(ROUTES.PROFILE);
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  return (
    <div className="mx-auto max-w-lg">
      <h1 className="mb-2 font-display text-2xl font-bold gradient-text">Become a vendor</h1>
      <p className="mb-6 text-sm text-slate-400">
        Register your brand on the marketplace. After Super Admin approval you become an Admin (vendor) and can list
        products for review.
      </p>
      <form onSubmit={handleSubmit((d) => mutation.mutate(d))} className="surface-card space-y-4 p-6">
        <Input label="Brand / company name" error={errors.brandName?.message} {...register('brandName')} />
        <div>
          <label className="mb-1 block text-sm font-medium text-slate-300">Description</label>
          <textarea
            {...register('description')}
            rows={3}
            className="w-full rounded-lg border border-neutral-700 bg-neutral-900 px-3 py-2 text-sm text-white"
          />
        </div>
        <Button type="submit" className="w-full" isLoading={mutation.isPending}>
          Submit application
        </Button>
        <p className="text-center text-xs text-slate-500">
          <Link to={ROUTES.PROFILE} className="text-primary-300 hover:underline">
            Back to profile
          </Link>
        </p>
      </form>
    </div>
  );
}
