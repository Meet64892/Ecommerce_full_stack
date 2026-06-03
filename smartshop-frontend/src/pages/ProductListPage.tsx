/**
 * ProductListPage.tsx — Infinite scroll catalog with URL-synced filters
 *
 * KEY CONCEPTS:
 * - useInfiniteQuery: Pages accumulate in `data.pages`; fetchNextPage loads the next slice.
 * - getNextPageParam: Returns next page number or undefined when `last` is true.
 * - useSearchParams: Shareable filter state in the URL (browser back works).
 * - useMemo: Memoizes flattened product list from pages.
 * - staleTime (global): Data stays fresh 5 min — stale-while-revalidate pattern.
 */

import { useCallback, useMemo, useEffect } from 'react';
import { useInfiniteQuery, useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useInView } from 'react-intersection-observer';
import { ErrorBoundary } from 'react-error-boundary';
import { productApi } from '@api/productApi';
import { ProductGrid } from '@components/product/ProductGrid';
import { ProductFilters } from '@components/product/ProductFilters';
import { ProductSearch } from '@components/product/ProductSearch';
import { ProductCard } from '@components/product/ProductCard';
import { ErrorFallback } from '@components/common/ErrorFallback';
import { Button } from '@components/ui/Button';
import { Select } from '@components/ui/Select';
import { PAGE_SIZE, SORT_OPTIONS } from '@utils/constants';
import type { ProductFilters as Filters } from '@/types/product.types';
import { useDebounce } from '@hooks/useDebounce';
import { PackageOpen } from 'lucide-react';

export default function ProductListPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const searchInput = searchParams.get('q') ?? '';
  const debouncedQuery = useDebounce(searchInput, 400);
  const sort = searchParams.get('sort') ?? 'createdAt,desc';
  const categoryId = searchParams.get('categoryId') ?? undefined;
  const minPrice = searchParams.get('minPrice') ? Number(searchParams.get('minPrice')) : undefined;
  const maxPrice = searchParams.get('maxPrice') ? Number(searchParams.get('maxPrice')) : undefined;
  const minRating = searchParams.get('minRating') ? Number(searchParams.get('minRating')) : undefined;

  const filters: Filters = { categoryId, minPrice, maxPrice, minRating };

  const { data: categories = [] } = useQuery({
    queryKey: ['categories'],
    queryFn: () => productApi.getCategories(),
  });

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
    isError,
    refetch,
    isFetching,
  } = useInfiniteQuery({
    queryKey: ['products', debouncedQuery, filters, sort],
    queryFn: ({ pageParam = 0 }) => {
      const hasFilters = !!(categoryId || minPrice || maxPrice || minRating);
      if (debouncedQuery.trim() || hasFilters) {
        return productApi.search(debouncedQuery, filters, pageParam, PAGE_SIZE, sort);
      }
      return productApi.getAll({ page: pageParam, size: PAGE_SIZE, sort });
    },
    initialPageParam: 0,
    getNextPageParam: (lastPage) => (lastPage.last ? undefined : lastPage.number + 1),
  });

  const products = useMemo(
    () => data?.pages.flatMap((p) => p.content) ?? [],
    [data],
  );

  const { ref: sentinelRef, inView } = useInView({ threshold: 0 });

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage) {
      void fetchNextPage();
    }
  }, [inView, hasNextPage, isFetchingNextPage, fetchNextPage]);

  const updateParams = useCallback(
    (updates: Record<string, string | undefined>) => {
      setSearchParams((prev) => {
        const next = new URLSearchParams(prev);
        Object.entries(updates).forEach(([key, value]) => {
          if (value === undefined || value === '') next.delete(key);
          else next.set(key, value);
        });
        return next;
      });
    },
    [setSearchParams],
  );

  const onFiltersChange = (next: Filters) => {
    updateParams({
      categoryId: next.categoryId,
      minPrice: next.minPrice?.toString(),
      maxPrice: next.maxPrice?.toString(),
      minRating: next.minRating?.toString(),
    });
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -16 }}
      className="w-full space-y-6 px-6 md:px-12"
    >
      <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <h1 className="text-2xl font-bold text-white">Shop</h1>
        <div className="flex flex-1 flex-col gap-3 sm:flex-row sm:items-center lg:max-w-xl">
          <ProductSearch
            value={searchInput}
            onChange={(q) => updateParams({ q })}
            isSearching={isFetching && !!debouncedQuery}
          />
          <Select
            label="Sort"
            value={sort}
            onChange={(v) => updateParams({ sort: v })}
            options={SORT_OPTIONS.map((o) => ({ value: o.value, label: o.label }))}
            className="sm:w-48"
          />
        </div>
      </div>

      <div className="grid gap-8 lg:grid-cols-[240px_1fr]">
        <ProductFilters filters={filters} categories={categories} onChange={onFiltersChange} />

        <div>
          <ErrorBoundary FallbackComponent={ErrorFallback} onReset={() => refetch()}>
            {isLoading ? (
              <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
                {Array.from({ length: 6 }).map((_, i) => (
                  <ProductCard.Skeleton key={i} />
                ))}
              </div>
            ) : isError ? (
              <div className="rounded-xl border border-red-200 bg-red-50 p-8 text-center">
                <p className="text-red-800">Failed to load products.</p>
                <Button className="mt-4" onClick={() => refetch()}>
                  Retry
                </Button>
              </div>
            ) : products.length === 0 ? (
              <div className="flex flex-col items-center py-16 text-slate-500">
                <PackageOpen className="mb-4 h-16 w-16 text-slate-600 transition-transform duration-300 hover:scale-105" />
                <p>No products match your filters.</p>
              </div>
            ) : (
              <>
                <ProductGrid products={products} />
                <div ref={sentinelRef} className="py-8 text-center">
                  {isFetchingNextPage && <span className="text-sm text-gray-500">Loading more…</span>}
                </div>
              </>
            )}
          </ErrorBoundary>
        </div>
      </div>
    </motion.div>
  );
}
