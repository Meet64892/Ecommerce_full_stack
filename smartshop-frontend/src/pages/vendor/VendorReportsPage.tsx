import { motion } from 'framer-motion';

export default function VendorReportsPage() {
  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
      <h1 className="font-display text-2xl font-bold gradient-text">Reports</h1>
      <p className="mt-2 text-slate-400">Sales, product performance, revenue, and inventory reports.</p>
    </motion.div>
  );
}
