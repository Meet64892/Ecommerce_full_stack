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
      className="group flex flex-col overflow-hidden rounded-none border border-neutral-800 bg-neutral-900 transition-colors hover:border-neutral-600 hover:bg-neutral-900/80"
    >
      <Link to={`/products/${product.id}`} className="block p-5">
        <div className="mb-4 flex aspect-square items-center justify-center border border-neutral-800 bg-neutral-950 text-5xl font-bold text-neutral-600">
          {product.name.charAt(0)}
        </div>
        <h3 className="font-semibold text-white group-hover:text-neutral-200">{product.name}</h3>
        <p className="mt-1 text-sm text-neutral-500">{truncate(product.description, 80)}</p>
        <div className="mt-2 flex items-center gap-1 text-sm text-neutral-300">
          <Star className="h-4 w-4 fill-white text-white" />
          <span>{product.rating.toFixed(1)}</span>
          <span className="text-neutral-600">· {product.categoryName}</span>
        </div>
        <p className="mt-3 text-xl font-bold text-white">{formatCurrency(product.price)}</p>
      </Link>
      <div className="mt-auto border-t border-neutral-800 p-4">
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
    <div className="border border-neutral-800 bg-neutral-900 p-5">
      <Skeleton className="mb-4 aspect-square w-full rounded-none" />
      <Skeleton className="mb-2 h-5 w-3/4" />
      <Skeleton.Text lines={2} />
      <Skeleton className="mt-4 h-10 w-full rounded-lg" />
    </div>
  );
}

export const ProductCard = Object.assign(memo(ProductCardComponent), {
  Skeleton: ProductCardSkeleton,
});
