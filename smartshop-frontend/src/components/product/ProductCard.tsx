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
  hidden: { opacity: 0, y: 24 },
  show: { opacity: 1, y: 0 },
};

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
      whileHover={{ y: -4 }}
      transition={{ type: 'spring', stiffness: 400, damping: 25 }}
      className="group card-interactive flex flex-col overflow-hidden"
    >
      <Link to={`/products/${product.id}`} className="block p-5">
        <div className="relative mb-4 flex aspect-square items-center justify-center overflow-hidden rounded-lg border border-slate-700/50 bg-surface-muted">
          <div
            className="absolute inset-0 bg-gradient-accent opacity-0 transition-opacity duration-300 group-hover:opacity-10"
            aria-hidden
          />
          <span className="relative text-5xl font-bold text-primary-400/80 transition-all duration-300 group-hover:scale-110 group-hover:text-primary-300">
            {product.name.charAt(0)}
          </span>
        </div>
        <h3 className="font-semibold text-slate-100 transition-colors duration-200 group-hover:text-primary-300">
          {product.name}
        </h3>
        <p className="mt-1 text-sm text-slate-500">{truncate(product.description, 80)}</p>
        <div className="mt-2 flex items-center gap-1 text-sm text-slate-400">
          <Star className="h-4 w-4 fill-amber-400 text-amber-400 transition-transform duration-200 group-hover:scale-110" />
          <span>{product.rating.toFixed(1)}</span>
          <span className="text-slate-600">· {product.categoryName}</span>
        </div>
        <p className="mt-3 text-xl font-bold gradient-text">{formatCurrency(product.price)}</p>
      </Link>
      <div className="mt-auto border-t border-slate-700/50 p-4">
        <Button
          className="w-full"
          variant={isInCart(product.id) ? 'secondary' : 'primary'}
          onClick={handleAdd}
        >
          <ShoppingCart className="h-4 w-4 transition-transform duration-200 group-hover:scale-110" />
          {isInCart(product.id) ? 'In cart' : 'Add to cart'}
        </Button>
      </div>
    </motion.article>
  );
}

function ProductCardSkeleton() {
  return (
    <div className="surface-card p-5">
      <Skeleton className="mb-4 aspect-square w-full rounded-lg" />
      <Skeleton className="mb-2 h-5 w-3/4" />
      <Skeleton.Text lines={2} />
      <Skeleton className="mt-4 h-10 w-full rounded-lg" />
    </div>
  );
}

export const ProductCard = Object.assign(memo(ProductCardComponent), {
  Skeleton: ProductCardSkeleton,
});
