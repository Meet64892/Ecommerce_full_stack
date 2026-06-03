import { motion } from 'framer-motion';

export default function SuperAdminUsersPage() {
  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
      <h1 className="font-display text-2xl font-bold gradient-text">Customer management</h1>
      <p className="mt-2 text-slate-400">
        View customers, suspend accounts, and support requests. Connect to the paginated users API for full listing.
      </p>
    </motion.div>
  );
}
