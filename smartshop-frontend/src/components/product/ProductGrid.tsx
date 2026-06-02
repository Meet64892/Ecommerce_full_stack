/**
 * ProductGrid.tsx — Responsive product grid with stagger animation
 *
 * LEARNING NOTE: We use infinite scroll instead of rendering 1000 DOM nodes (virtual scroll
 * would be the next step at very large catalogs).
 */

import { motion } from 'framer-motion';
import type { Product } from '@/types/product.types';
import { ProductCard } from './ProductCard';

const containerVariants = {
  hidden: {},
  show: { transition: { staggerChildren: 0.05 } },
};

export function ProductGrid({ products }: { products: Product[] }) {
  return (
    <motion.div
      variants={containerVariants}
      initial="hidden"
      animate="show"
      className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4"
    >
      {products.map((product) => (
        <ProductCard key={product.id} product={product} />
      ))}
    </motion.div>
  );
}
