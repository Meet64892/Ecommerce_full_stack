/**
 * ProductDetailPage.tsx — Single product with inventory, gallery, related items
 */

import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { ErrorBoundary } from 'react-error-boundary';
import { productApi } from '@api/productApi';
import { inventoryApi } from '@api/inventoryApi';
import { ProductImageGallery } from '@components/product/ProductImageGallery';
import { ProductGrid } from '@components/product/ProductGrid';
import { ErrorFallback } from '@components/common/ErrorFallback';
import { Button } from '@components/ui/Button';
import { Skeleton } from '@components/ui/Skeleton';
import { useCart } from '@hooks/useCart';
import { formatCurrency } from '@utils/formatters';
import toast from 'react-hot-toast';

export default function ProductDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [quantity, setQuantity] = useState(1);
  const { addToCart } = useCart();
  const queryClient = useQueryClient();

  const { data: product, isLoading } = useQuery({
    queryKey: ['product', id],
    queryFn: () => productApi.getById(id!),
    enabled: !!id,
  });

  const { data: inventory } = useQuery({
    queryKey: ['inventory', id],
    queryFn: () => inventoryApi.getByProductId(id!),
    enabled: !!id,
  });

  const { data: related } = useQuery({
    queryKey: ['products', 'related', product?.categoryId],
    queryFn: () => productApi.getByCategory(product!.categoryId, 0, 4),
    enabled: !!product?.categoryId,
    select: (page) => page.content.filter((p) => p.id !== product?.id).slice(0, 4),
  });

  const maxQty = inventory?.salableQuantity ?? 99;

  const handleAddToCart = () => {
    if (!product) return;
    if (quantity > maxQty) {
      toast.error(`Only ${maxQty} available`);
      return;
    }
    addToCart(product, quantity, { silent: false });
    void queryClient.invalidateQueries({ queryKey: ['cart'] });
  };

  if (isLoading) {
    return (
      <div className="grid gap-8 lg:grid-cols-2">
        <Skeleton className="aspect-square w-full" />
        <div className="space-y-4">
          <Skeleton className="h-8 w-2/3" />
          <Skeleton.Text lines={4} />
          <Skeleton className="h-12 w-40" />
        </div>
      </div>
    );
  }

  if (!product) {
    return <p>Product not found.</p>;
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      className="space-y-12"
    >
      <div className="grid gap-8 lg:grid-cols-2">
        <ErrorBoundary FallbackComponent={ErrorFallback}>
          <ProductImageGallery productName={product.name} />
        </ErrorBoundary>
        <div>
          <h1 className="text-3xl font-bold text-gray-900">{product.name}</h1>
          <p className="mt-2 text-2xl font-semibold text-primary-600">
            {formatCurrency(product.price)}
          </p>
          <p className="mt-4 text-gray-600">{product.description}</p>
          <p className="mt-2 text-sm text-gray-500">
            SKU: {product.stockKeepingUnit} · {product.categoryName}
          </p>
          {inventory && (
            <p className="mt-2 text-sm text-green-700">
              {inventory.salableQuantity} in stock
            </p>
          )}
          <div className="mt-6 flex items-center gap-4">
            <label className="text-sm font-medium">Qty</label>
            <input
              type="number"
              min={1}
              max={maxQty}
              value={quantity}
              onChange={(e) => setQuantity(Number(e.target.value))}
              className="w-20 rounded-lg border px-3 py-2"
            />
          </div>
          <motion.div whileTap={{ scale: 0.95 }} className="mt-6 inline-block">
            <Button size="lg" onClick={handleAddToCart} className="cart-bounce">
              Add to cart
            </Button>
          </motion.div>
        </div>
      </div>

      {related && related.length > 0 && (
        <ErrorBoundary FallbackComponent={ErrorFallback}>
          <section>
            <h2 className="mb-4 text-xl font-semibold">Related products</h2>
            <ProductGrid products={related} />
          </section>
        </ErrorBoundary>
      )}
    </motion.div>
  );
}
