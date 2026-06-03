/**
 * RegisterForm.tsx — Registration form
 */

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link } from 'react-router-dom';
import { Input } from '@components/ui/Input';
import { Button } from '@components/ui/Button';
import { useAuth } from '@hooks/useAuth';
import { ROUTES } from '@utils/constants';
import toast from 'react-hot-toast';
import { parseApiError } from '@utils/errorHandler';

const schema = z.object({
  email: z.string().email(),
  password: z.string().min(8, 'At least 8 characters'),
  firstName: z.string().min(2),
  lastName: z.string().min(2),
});

type FormValues = z.infer<typeof schema>;

export function RegisterForm({ onSuccess }: { onSuccess?: () => void }) {
  const { register: registerUser, isLoading } = useAuth();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  const onSubmit = handleSubmit(async (values) => {
    try {
      await registerUser(values);
      toast.success('Account created!');
      onSuccess?.();
    } catch (error) {
      toast.error(parseApiError(error));
    }
  });

  return (
    <form onSubmit={onSubmit} className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <Input label="First name" error={errors.firstName?.message} {...register('firstName')} />
        <Input label="Last name" error={errors.lastName?.message} {...register('lastName')} />
      </div>
      <Input label="Email" type="email" error={errors.email?.message} {...register('email')} />
      <Input
        label="Password"
        type="password"
        error={errors.password?.message}
        {...register('password')}
      />
      <Button type="submit" className="w-full" isLoading={isLoading}>
        Create account
      </Button>
      <p className="text-center text-sm text-slate-500">
        Already have an account?{' '}
        <Link to={ROUTES.LOGIN} className="link-accent">
          Sign in
        </Link>
      </p>
    </form>
  );
}
