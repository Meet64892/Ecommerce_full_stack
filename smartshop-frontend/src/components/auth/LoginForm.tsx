/**
 * LoginForm.tsx — Email/password login with react-hook-form + zod
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
  email: z.string().email('Enter a valid email'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type FormValues = z.infer<typeof schema>;

export function LoginForm({ onSuccess }: { onSuccess?: () => void }) {
  const { login, isLoading } = useAuth();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  const onSubmit = handleSubmit(async (values) => {
    try {
      await login(values);
      toast.success('Welcome back!');
      onSuccess?.();
    } catch (error) {
      toast.error(parseApiError(error));
    }
  });

  return (
    <form onSubmit={onSubmit} className="space-y-4">
      <Input label="Email" type="email" error={errors.email?.message} {...register('email')} />
      <Input
        label="Password"
        type="password"
        error={errors.password?.message}
        {...register('password')}
      />
      <Button type="submit" className="w-full" isLoading={isLoading}>
        Sign in
      </Button>
      <p className="text-center text-sm text-gray-600">
        No account?{' '}
        <Link to={ROUTES.REGISTER} className="text-primary-600 hover:underline">
          Register
        </Link>
      </p>
    </form>
  );
}
