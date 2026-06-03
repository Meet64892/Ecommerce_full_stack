import { motion } from 'framer-motion';

export default function VendorPromotionsPage() {
  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}>
      <h1 className="font-display text-2xl font-bold gradient-text">Promotions</h1>
      <p className="mt-2 text-slate-400">Coupons, discounts, flash sales, and bundle offers for your brand.</p>
    </motion.div>
  );
}
