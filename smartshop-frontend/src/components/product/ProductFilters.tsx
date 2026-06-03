/**
 * ProductFilters.tsx — Category, price, rating filters synced to URL
 */

import type { ProductFilters as Filters } from '@/types/product.types';
import { Input } from '@components/ui/Input';
import type { Category } from '@/types/product.types';

export interface ProductFiltersProps {
  filters: Filters;
  categories: Category[];
  onChange: (filters: Filters) => void;
}

export function ProductFilters({ filters, categories, onChange }: ProductFiltersProps) {
  return (
    <aside className="surface-card space-y-6 p-4 transition-shadow duration-300 hover:shadow-glow-sm lg:sticky lg:top-24">
      <h2 className="font-semibold text-slate-100">Filters</h2>

      <div>
        <label className="mb-1 block text-sm font-medium text-slate-400">Category</label>
        <select
          value={filters.categoryId ?? ''}
          onChange={(e) =>
            onChange({ ...filters, categoryId: e.target.value || undefined })
          }
          className="input-glow w-full rounded-lg border border-slate-600/60 bg-surface-muted px-3 py-2 text-sm text-slate-100 transition-all duration-200 hover:border-slate-500 focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/25"
        >
          <option value="">All categories</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>
      </div>

      <div className="grid grid-cols-2 gap-2">
        <Input
          label="Min price"
          type="number"
          min={0}
          value={filters.minPrice ?? ''}
          onChange={(e) =>
            onChange({
              ...filters,
              minPrice: e.target.value ? Number(e.target.value) : undefined,
            })
          }
        />
        <Input
          label="Max price"
          type="number"
          min={0}
          value={filters.maxPrice ?? ''}
          onChange={(e) =>
            onChange({
              ...filters,
              maxPrice: e.target.value ? Number(e.target.value) : undefined,
            })
          }
        />
      </div>

      <div>
        <label className="mb-1 block text-sm font-medium text-slate-400">
          Min rating ({filters.minRating ?? 0})
        </label>
        <input
          type="range"
          min={0}
          max={5}
          step={0.5}
          value={filters.minRating ?? 0}
          onChange={(e) =>
            onChange({ ...filters, minRating: Number(e.target.value) || undefined })
          }
          className="w-full accent-primary-500"
        />
      </div>
    </aside>
  );
}
