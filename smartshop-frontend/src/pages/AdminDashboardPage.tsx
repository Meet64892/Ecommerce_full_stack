/**
 * AdminDashboardPage.tsx — Super admin platform overview
 */

import { motion } from 'framer-motion';
import { Shield, Users, Package } from 'lucide-react';
import { useAuth } from '@hooks/useAuth';
import { ROLE_LABELS } from '@utils/roles';

export default function AdminDashboardPage() {
  const { user } = useAuth();

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      className="mx-auto max-w-3xl"
    >
      <div className="mb-8 flex items-center gap-3">
        <Shield className="h-8 w-8 text-primary-400" />
        <div>
          <h1 className="font-display text-2xl font-bold gradient-text">Admin dashboard</h1>
          <p className="text-sm text-slate-400">
            Signed in as {user?.fullName} · {ROLE_LABELS.SUPER_ADMIN}
          </p>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div className="surface-card p-6">
          <Users className="mb-3 h-6 w-6 text-accent-300" />
          <h2 className="font-semibold text-slate-100">User management</h2>
          <p className="mt-2 text-sm text-slate-400">
            Manage customers, brand owners, and platform settings across SmartShop.
          </p>
        </div>
        <div className="surface-card p-6">
          <Package className="mb-3 h-6 w-6 text-accent-300" />
          <h2 className="font-semibold text-slate-100">Catalog oversight</h2>
          <p className="mt-2 text-sm text-slate-400">
            Review and moderate all products listed by brands on the marketplace.
          </p>
        </div>
      </div>

      <p className="mt-8 text-center text-xs text-slate-500">
        Full admin APIs will connect here. You have the highest access level on the platform.
      </p>
    </motion.div>
  );
}
