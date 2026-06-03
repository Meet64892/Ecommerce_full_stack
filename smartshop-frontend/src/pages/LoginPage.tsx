/**
 * LoginPage.tsx — Sign in with redirect to originally requested route
 */

import { useLocation, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { LoginForm } from '@components/auth/LoginForm';
import { ROUTES } from '@utils/constants';

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const from = (location.state as { from?: { pathname: string } })?.from?.pathname ?? ROUTES.HOME;

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      className="surface-card mx-auto max-w-md p-8 shadow-card-hover transition-shadow duration-300 hover:shadow-glow-sm"
    >
      <h1 className="mb-6 text-center font-display text-2xl font-bold gradient-text">Sign in</h1>
      <LoginForm onSuccess={() => navigate(from, { replace: true })} />
    </motion.div>
  );
}
