/**
 * ProductSearch.tsx — Debounced search with recent history (dropdown on focus only)
 */

import { forwardRef, useCallback, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { Search, X } from 'lucide-react';
import { useDebounce } from '@hooks/useDebounce';
import { useLocalStorage } from '@hooks/useLocalStorage';
import { useClickOutside } from '@hooks/useClickOutside';
import { cn } from '@utils/cn';

export interface ProductSearchProps {
  value: string;
  onChange: (value: string) => void;
  isSearching?: boolean;
}

export const ProductSearch = forwardRef<HTMLInputElement, ProductSearchProps>(
  function ProductSearch({ value, onChange, isSearching }, ref) {
    const containerRef = useRef<HTMLDivElement>(null);
    const inputRef = useRef<HTMLInputElement>(null);
    const [focused, setFocused] = useState(false);
    useImperativeHandle(ref, () => inputRef.current as HTMLInputElement);

    const [recent, setRecent] = useLocalStorage<string[]>('smartshop-recent-searches', []);
    const debounced = useDebounce(value, 400);

    const saveRecentSearch = useCallback(
      (searchTerm: string) => {
        if (!searchTerm.trim()) return;
        setRecent((prev) => [searchTerm, ...prev.filter((s) => s !== searchTerm)].slice(0, 5));
      },
      [setRecent],
    );

    useEffect(() => {
      if (!debounced.trim()) return;
      saveRecentSearch(debounced);
    }, [debounced, saveRecentSearch]);

    const closeDropdown = useCallback(() => setFocused(false), []);
    useClickOutside(containerRef, closeDropdown);

    const showRecent = focused && recent.length > 0 && !value;

    return (
      <div ref={containerRef} className="relative w-full">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-neutral-500" />
        <input
          ref={inputRef}
          type="search"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          onFocus={() => setFocused(true)}
          placeholder="Search products…"
          className={cn(
            'w-full rounded-lg border border-neutral-700 bg-neutral-900 py-2.5 pl-10 pr-10 text-sm text-white',
            'focus:border-white focus:outline-none focus:ring-2 focus:ring-white/20',
          )}
        />
        {isSearching && (
          <span className="absolute right-10 top-1/2 -translate-y-1/2 text-xs text-neutral-500">
            Searching…
          </span>
        )}
        {value && (
          <button
            type="button"
            onClick={() => onChange('')}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-neutral-500 hover:text-white"
            aria-label="Clear search"
          >
            <X className="h-4 w-4" />
          </button>
        )}
        {showRecent && (
          <ul
            className="glass-panel absolute z-10 mt-1 w-full rounded-lg py-1 shadow-card-hover animate-slide-down"
            role="listbox"
            aria-label="Recent searches"
          >
            <li className="px-4 py-1.5 text-xs font-medium uppercase tracking-wide text-slate-500">
              Recent
            </li>
            {recent.map((term) => (
              <li key={term}>
                <button
                  type="button"
                  className="w-full px-4 py-2 text-left text-sm text-slate-300 transition-colors hover:bg-primary-600/15 hover:text-white"
                  onMouseDown={(e) => e.preventDefault()}
                  onClick={() => {
                    onChange(term);
                    setFocused(false);
                  }}
                >
                  {term}
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    );
  },
);
