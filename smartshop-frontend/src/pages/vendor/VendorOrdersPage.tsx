import { motion } from 'framer-motion';

export default function VendorOrdersPage() {
  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
      <h1 className="font-display text-2xl font-bold gradient-text">Your orders</h1>
      <p className="mt-2 text-slate-400">
        View and process orders for your brand only. Multi-vendor order routing will link order items to your brand.
      </p>
    </motion.div>
  );
}
