import { motion } from 'framer-motion';

export default function SuperAdminSettingsPage() {
  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
      <h1 className="font-display text-2xl font-bold gradient-text">System settings</h1>
      <p className="mt-2 text-slate-400">
        Tax, shipping, payment gateways, email, notifications, and security settings for the marketplace.
      </p>
    </motion.div>
  );
}
