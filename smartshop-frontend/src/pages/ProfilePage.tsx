/**
 * ProfilePage.tsx — User profile view/edit
 */

import { useForm } from 'react-hook-form';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { useAuth } from '@hooks/useAuth';
import { userApi } from '@api/userApi';
import { Input } from '@components/ui/Input';
import { Button } from '@components/ui/Button';
import { Badge } from '@components/ui/Badge';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';
import { Link } from 'react-router-dom';
import { ROUTES } from '@utils/constants';
import { ROLE_LABELS, isCustomer } from '@utils/roles';

export default function ProfilePage() {
  const { user, refreshUser } = useAuth();
  const queryClient = useQueryClient();

  const { data: profile } = useQuery({
    queryKey: ['user', user?.id],
    queryFn: () => userApi.getById(user!.id),
    enabled: !!user?.id,
    initialData: user ?? undefined,
  });

  const { register, handleSubmit, reset } = useForm({
    values: {
      firstName: profile?.firstName ?? '',
      lastName: profile?.lastName ?? '',
      email: profile?.email ?? '',
    },
  });

  const updateMutation = useMutation({
    mutationFn: (data: { firstName: string; lastName: string; email: string }) =>
      userApi.update(user!.id, data),
    onSuccess: async () => {
      await refreshUser();
      void queryClient.invalidateQueries({ queryKey: ['user', user?.id] });
      toast.success('Profile updated');
    },
    onError: (e) => toast.error(parseApiError(e)),
  });

  return (
    <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="mx-auto max-w-lg">
      <h1 className="mb-2 font-display text-2xl font-bold gradient-text">My profile</h1>
      {profile?.role && <Badge className="mb-6">{ROLE_LABELS[profile.role]}</Badge>}
      <form
        onSubmit={handleSubmit((data) => updateMutation.mutate(data))}
        className="surface-card space-y-4 p-6 transition-shadow duration-300 hover:shadow-glow-sm"
      >
        <Input label="First name" {...register('firstName')} />
        <Input label="Last name" {...register('lastName')} />
        <Input label="Email" type="email" {...register('email')} />
        <Button type="submit" isLoading={updateMutation.isPending}>
          Save changes
        </Button>
        <Button type="button" variant="ghost" onClick={() => reset()}>
          Reset
        </Button>
      </form>
      {profile && isCustomer(profile.role) && (
        <p className="mt-6 text-center text-sm text-slate-400">
          Want to sell on SmartShop?{' '}
          <Link to={ROUTES.VENDOR_APPLY} className="text-primary-300 hover:underline">
            Apply to become a vendor
          </Link>
        </p>
      )}
    </motion.div>
  );
}
