/**
 * HomePage.tsx — Landing page with featured products
 */

import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ErrorBoundary } from 'react-error-boundary';
import { productApi } from '@api/productApi';
import { ProductGrid } from '@components/product/ProductGrid';
import { ErrorFallback } from '@components/common/ErrorFallback';
import { Button } from '@components/ui/Button';
import { Spinner } from '@components/ui/Spinner';
import { ROUTES } from '@utils/constants';
import { BarChart, Bar, XAxis, YAxis, ResponsiveContainer, Tooltip as RechartsTooltip } from 'recharts';

const chartData = [
  { name: 'Mon', orders: 12 },
  { name: 'Tue', orders: 19 },
  { name: 'Wed', orders: 8 },
  { name: 'Thu', orders: 24 },
  { name: 'Fri', orders: 32 },
];

/**
 * HomePage — Hero + featured products (demonstrates recharts in dashboard teaser)
 */
export default function HomePage() {
  const { data: featured, isLoading } = useQuery({
    queryKey: ['products', 'featured'],
    queryFn: () => productApi.getFeatured(),
  });

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -16 }}
      transition={{ duration: 0.2 }}
      className="space-y-12"
    >
      <section className="rounded-2xl bg-gradient-to-r from-primary-600 to-primary-800 px-6 py-16 text-white md:px-12">
        <h1 className="text-3xl font-bold md:text-5xl">Welcome to SmartShop</h1>
        <p className="mt-4 max-w-xl text-primary-100">
          A production-grade React storefront wired to Spring Boot microservices — read the code
          like a textbook.
        </p>
        <Link to={ROUTES.PRODUCTS} className="mt-6 inline-block">
          <Button variant="secondary" size="lg">
            Shop now
          </Button>
        </Link>
      </section>

      <section className="rounded-xl border bg-white p-6">
        <h2 className="mb-4 text-lg font-semibold">Weekly orders (demo chart)</h2>
        <div className="h-48">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData}>
              <XAxis dataKey="name" />
              <YAxis />
              <RechartsTooltip />
              <Bar dataKey="orders" fill="#2563eb" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </section>

      <section>
        <h2 className="mb-6 text-2xl font-bold text-gray-900">Featured products</h2>
        <ErrorBoundary FallbackComponent={ErrorFallback}>
          {isLoading ? (
            <div className="flex justify-center py-12">
              <Spinner size="lg" />
            </div>
          ) : featured && featured.length > 0 ? (
            <ProductGrid products={featured} />
          ) : (
            <p className="text-gray-500">No featured products yet. Start the product-service backend.</p>
          )}
        </ErrorBoundary>
      </section>
    </motion.div>
  );
}
