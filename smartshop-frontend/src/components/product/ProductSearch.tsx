/**
 * ProductSearch.tsx — Debounced search with recent history and Ctrl+K
 */

import { forwardRef, useEffect, useImperativeHandle, useRef } from 'react';
import { Search, X } from 'lucide-react';
import { useDebounce } from '@hooks/useDebounce';
import { useLocalStorage } from '@hooks/useLocalStorage';
import { cn } from '@utils/cn';

import { useCallback } from 'react';


export interface ProductSearchProps {
  value: string;
  onChange: (value: string) => void;
  isSearching?: boolean;
}

export const ProductSearch = forwardRef<HTMLInputElement, ProductSearchProps>(
  function ProductSearch({ value, onChange, isSearching }, ref) {
    const inputRef = useRef<HTMLInputElement>(null);
    useImperativeHandle(ref, () => inputRef.current as HTMLInputElement);

    const [recent, setRecent] = useLocalStorage<string[]>('smartshop-recent-searches', []);
    const debounced = useDebounce(value, 400);


// Create a stable wrapper for setRecent
const saveRecentSearch = useCallback((searchTerm: string) => {
  if (!searchTerm.trim()) return;
  
  setRecent((prev) => {
    const next = [searchTerm, ...prev.filter((s) => s !== searchTerm)].slice(0, 5);
    return next;
  });
}, [setRecent]); // This dependency is still problematic but less likely to cause loops

useEffect(() => {
  if (!debounced.trim()) return;
  saveRecentSearch(debounced);
}, [debounced, saveRecentSearch]);

    useEffect(() => {
      if (!debounced.trim()) return;
      setRecent((prev) => {
        const next = [debounced, ...prev.filter((s) => s !== debounced)].slice(0, 5);
        return next;
      });
    }, [debounced, setRecent]);

    // useEffect(() => {
    //   const onKeyDown = (e: KeyboardEvent) => {
    //     if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
    //       e.preventDefault();
    //       inputRef.current?.focus();
    //     }
    //   };
    //   window.addEventListener('keydown', onKeyDown);
    //   return () => window.removeEventListener('keydown', onKeyDown);
    // }, []);

    return (
      <div className="relative w-full">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-neutral-500" />
        <input
          ref={inputRef}
          type="search"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder="Search products… (Ctrl+K)"
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
        {recent.length > 0 && !value && (
          <ul className="glass-panel absolute z-10 mt-1 w-full rounded-lg py-1 shadow-card-hover animate-slide-down">
            {recent.map((term) => (
              <li key={term}>
                <button
                  type="button"
                  className="w-full px-4 py-2 text-left text-sm text-slate-300 transition-colors hover:bg-primary-600/15 hover:text-white"
                  onClick={() => onChange(term)}
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
