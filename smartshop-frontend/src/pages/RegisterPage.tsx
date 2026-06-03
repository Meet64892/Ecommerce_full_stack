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
      className="surface-card mx-auto max-w-md p-8 shadow-card-hover transition-shadow duration-300 hover:shadow-glow-sm"
    >
      <h1 className="mb-6 text-center font-display text-2xl font-bold gradient-text">Create account</h1>
      <RegisterForm onSuccess={() => navigate(ROUTES.HOME, { replace: true })} />
    </motion.div>
  );
}
