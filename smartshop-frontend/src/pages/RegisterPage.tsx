/**
 * RegisterPage.tsx — Create account
 */

import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { RegisterForm } from '@components/auth/RegisterForm';
import { ROUTES } from '@utils/constants';

export default function RegisterPage() {
  const navigate = useNavigate();

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      className="mx-auto max-w-md border border-neutral-800 bg-neutral-900 p-8"
    >
      <h1 className="mb-6 text-center text-2xl font-bold text-white">Create account</h1>
      <RegisterForm onSuccess={() => navigate(ROUTES.HOME, { replace: true })} />
    </motion.div>
  );
}
