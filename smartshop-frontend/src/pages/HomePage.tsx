/**
 * HomePage.tsx — Landing page with featured products
 */

import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ErrorBoundary } from 'react-error-boundary';
import { Sparkles } from 'lucide-react';
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

const fadeUp = {
  hidden: { opacity: 0, y: 24 },
  show: (i: number) => ({
    opacity: 1,
    y: 0,
    transition: { delay: i * 0.1, duration: 0.45, ease: [0.16, 1, 0.3, 1] },
  }),
};

export default function HomePage() {
  const { data: featured, isLoading } = useQuery({
    queryKey: ['products', 'featured'],
    queryFn: () => productApi.getFeatured(),
  });

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.3 }}
      className="w-full space-y-0"
    >
      <section className="relative w-full overflow-hidden border-b border-slate-700/50 bg-mesh-hero px-6 py-24 md:px-12 md:py-32">
        <div
          className="hero-glow-orb pointer-events-none absolute -left-32 top-0 h-64 w-64 rounded-full bg-primary-600/30 blur-3xl"
          aria-hidden
        />
        <div
          className="hero-glow-orb pointer-events-none absolute -right-24 bottom-0 h-48 w-48 rounded-full bg-accent-400/20 blur-3xl"
          aria-hidden
          style={{ animationDelay: '1.5s' }}
        />

        <motion.div
          custom={0}
          variants={fadeUp}
          initial="hidden"
          animate="show"
          className="relative"
        >
          <span className="mb-4 inline-flex items-center gap-2 rounded-full border border-primary-500/30 bg-primary-600/10 px-4 py-1.5 text-sm text-primary-300">
            <Sparkles className="h-4 w-4 text-primary-400" />
            Modern microservices storefront
          </span>
          <h1 className="font-display text-4xl font-extrabold tracking-tight md:text-6xl lg:text-7xl">
            <span className="gradient-text animate-gradient-shift">SmartShop</span>
          </h1>
          <p className="mt-5 max-w-xl text-lg text-slate-400">
            A production-grade React storefront wired to Spring Boot microservices — read the code
            like a textbook.
          </p>
          <Link to={ROUTES.PRODUCTS} className="mt-10 inline-block">
            <Button variant="primary" size="lg" className="group">
              Shop now
              <span className="inline-block transition-transform duration-200 group-hover:translate-x-1">
                →
              </span>
            </Button>
          </Link>
        </motion.div>
      </section>

      <section className="w-full border-b border-slate-700/50 bg-surface/40 px-6 py-12 md:px-12">
        <motion.h2
          custom={1}
          variants={fadeUp}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true }}
          className="mb-6 text-lg font-semibold text-slate-200"
        >
          Weekly orders (demo chart)
        </motion.h2>
        <motion.div
          custom={2}
          variants={fadeUp}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true }}
          className="surface-card h-52 p-4 transition-shadow duration-300 hover:shadow-card-hover"
        >
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData}>
              <XAxis dataKey="name" stroke="#64748b" tick={{ fill: '#94a3b8' }} />
              <YAxis stroke="#64748b" tick={{ fill: '#94a3b8' }} />
              <RechartsTooltip
                contentStyle={{
                  background: '#1a1f2e',
                  border: '1px solid rgba(139, 92, 246, 0.4)',
                  borderRadius: '12px',
                  color: '#f1f5f9',
                  boxShadow: '0 0 20px -4px rgba(139, 92, 246, 0.35)',
                }}
                cursor={{ fill: 'rgba(139, 92, 246, 0.08)' }}
              />
              <Bar
                dataKey="orders"
                fill="url(#barGradient)"
                radius={[6, 6, 0, 0]}
              />
              <defs>
                <linearGradient id="barGradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#a78bfa" />
                  <stop offset="100%" stopColor="#22d3ee" />
                </linearGradient>
              </defs>
            </BarChart>
          </ResponsiveContainer>
        </motion.div>
      </section>

      <section className="w-full px-6 py-14 md:px-12">
        <motion.h2
          custom={0}
          variants={fadeUp}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true }}
          className="mb-8 font-display text-2xl font-bold text-slate-100"
        >
          Featured products
        </motion.h2>
        <ErrorBoundary FallbackComponent={ErrorFallback}>
          {isLoading ? (
            <div className="flex justify-center py-12">
              <Spinner size="lg" />
            </div>
          ) : featured && featured.length > 0 ? (
            <ProductGrid products={featured} />
          ) : (
            <p className="text-slate-500">
              No featured products yet. Start the product-service backend.
            </p>
          )}
        </ErrorBoundary>
      </section>
    </motion.div>
  );
}
