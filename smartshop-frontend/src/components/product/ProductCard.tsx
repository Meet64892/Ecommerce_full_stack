/**
 * ProductCard.tsx — Single product tile in grid
 */

import { memo, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ShoppingCart, Star } from 'lucide-react';
import type { Product } from '@/types/product.types';
import { formatCurrency, truncate } from '@utils/formatters';
import { Button } from '@components/ui/Button';
import { Skeleton } from '@components/ui/Skeleton';
import { useCart } from '@hooks/useCart';

export interface ProductCardProps {
  product: Product;
  onAddToCart?: (product: Product) => void;
}

const itemVariants = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0 },
};

/**
 * ProductCard — Memoized to avoid re-renders when sibling cards update in ProductGrid
 */
function ProductCardComponent({ product, onAddToCart }: ProductCardProps) {
  const { addToCart, isInCart } = useCart();

  const handleAdd = useCallback(
    (e: React.MouseEvent) => {
      e.preventDefault();
      if (onAddToCart) onAddToCart(product);
      else addToCart(product, 1);
    },
    [addToCart, onAddToCart, product],
  );

  return (
    <motion.article
      variants={itemVariants}
      className="group flex flex-col overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm transition-shadow hover:shadow-md"
    >
      <Link to={`/products/${product.id}`} className="block p-4">
        <div className="mb-3 flex aspect-square items-center justify-center rounded-lg bg-gradient-to-br from-primary-50 to-primary-100 text-4xl font-bold text-primary-300">
          {product.name.charAt(0)}
        </div>
        <h3 className="font-semibold text-gray-900 group-hover:text-primary-600">{product.name}</h3>
        <p className="mt-1 text-sm text-gray-500">{truncate(product.description, 80)}</p>
        <div className="mt-2 flex items-center gap-1 text-sm text-amber-600">
          <Star className="h-4 w-4 fill-current" />
          <span>{product.rating.toFixed(1)}</span>
          <span className="text-gray-400">· {product.categoryName}</span>
        </div>
        <p className="mt-2 text-lg font-bold text-primary-600">{formatCurrency(product.price)}</p>
      </Link>
      <div className="mt-auto border-t border-gray-100 p-4">
        <Button
          className="w-full"
          variant={isInCart(product.id) ? 'secondary' : 'primary'}
          onClick={handleAdd}
        >
          <ShoppingCart className="h-4 w-4" />
          {isInCart(product.id) ? 'In cart' : 'Add to cart'}
        </Button>
      </div>
    </motion.article>
  );
}

function ProductCardSkeleton() {
  return (
    <div className="rounded-xl border border-gray-200 bg-white p-4">
      <Skeleton className="mb-3 aspect-square w-full rounded-lg" />
      <Skeleton className="mb-2 h-5 w-3/4" />
      <Skeleton.Text lines={2} />
      <Skeleton className="mt-4 h-10 w-full rounded-lg" />
    </div>
  );
}

export const ProductCard = Object.assign(memo(ProductCardComponent), {
  Skeleton: ProductCardSkeleton,
});
