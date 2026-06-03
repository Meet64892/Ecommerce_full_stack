import { motion } from 'framer-motion';

export default function SuperAdminOrdersPage() {
  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
      <h1 className="font-display text-2xl font-bold gradient-text">All orders</h1>
      <p className="mt-2 text-slate-400">
        Platform-wide order tracking, disputes, refunds, and returns will appear here. Orders are currently stored per
        customer in the order service; multi-vendor order splits are planned next.
      </p>
    </motion.div>
  );
}
