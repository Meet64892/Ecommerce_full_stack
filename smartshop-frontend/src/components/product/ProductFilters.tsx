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
    <aside className="space-y-6 border border-neutral-800 bg-neutral-900 p-4 lg:sticky lg:top-24">
      <h2 className="font-semibold text-white">Filters</h2>

      <div>
        <label className="mb-1 block text-sm font-medium text-neutral-400">Category</label>
        <select
          value={filters.categoryId ?? ''}
          onChange={(e) =>
            onChange({ ...filters, categoryId: e.target.value || undefined })
          }
          className="w-full rounded-lg border border-neutral-700 bg-neutral-950 px-3 py-2 text-sm text-white"
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
        <label className="mb-1 block text-sm font-medium text-neutral-400">
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
          className="w-full accent-white"
        />
      </div>
    </aside>
  );
}
