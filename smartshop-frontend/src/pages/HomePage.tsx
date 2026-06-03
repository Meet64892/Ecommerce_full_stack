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
      className="w-full space-y-0"
    >
      <section className="w-full border-b border-neutral-800 bg-neutral-950 px-6 py-20 md:px-12 md:py-28">
        <h1 className="text-4xl font-bold tracking-tight text-white md:text-6xl">SmartShop</h1>
        <p className="mt-4 max-w-xl text-lg text-neutral-400">
          A production-grade React storefront wired to Spring Boot microservices — read the code
          like a textbook.
        </p>
        <Link to={ROUTES.PRODUCTS} className="mt-8 inline-block">
          <Button variant="primary" size="lg">
            Shop now
          </Button>
        </Link>
      </section>

      <section className="w-full border-b border-neutral-800 bg-neutral-900 px-6 py-10 md:px-12">
        <h2 className="mb-6 text-lg font-semibold text-white">Weekly orders (demo chart)</h2>
        <div className="h-48">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData}>
              <XAxis dataKey="name" stroke="#737373" />
              <YAxis stroke="#737373" />
              <RechartsTooltip
                contentStyle={{ background: '#171717', border: '1px solid #404040', color: '#fff' }}
              />
              <Bar dataKey="orders" fill="#ffffff" radius={[2, 2, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </section>

      <section className="w-full px-6 py-12 md:px-12">
        <h2 className="mb-8 text-2xl font-bold text-white">Featured products</h2>
        <ErrorBoundary FallbackComponent={ErrorFallback}>
          {isLoading ? (
            <div className="flex justify-center py-12">
              <Spinner size="lg" />
            </div>
          ) : featured && featured.length > 0 ? (
            <ProductGrid products={featured} />
          ) : (
            <p className="text-neutral-500">No featured products yet. Start the product-service backend.</p>
          )}
        </ErrorBoundary>
      </section>
    </motion.div>
  );
}
