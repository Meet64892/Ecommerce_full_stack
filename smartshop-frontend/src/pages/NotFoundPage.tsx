/**
 * NotFoundPage.tsx — 404 catch-all
 */

import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ROUTES } from '@utils/constants';

export default function NotFoundPage() {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="flex flex-col items-center py-24 text-center"
    >
      <p className="text-6xl font-bold text-primary-600">404</p>
      <h1 className="mt-4 text-xl font-semibold">Page not found</h1>
      <Link to={ROUTES.HOME} className="mt-6 text-primary-600 hover:underline">
        Back to home
      </Link>
    </motion.div>
  );
}
