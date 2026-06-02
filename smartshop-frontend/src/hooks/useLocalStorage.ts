/**
 * useLocalStorage.ts — Syncs React state with localStorage
 *
 * PURPOSE:
 * Survives page refresh; used for recent searches and preferences not in Zustand.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - JSON.parse/stringify cannot round-trip Date, undefined, or functions.
 * - storage event: Other tabs can update the same key — we listen for cross-tab sync.
 *
 * CONNECTED TO:
 * - ProductSearch (recent searches)
 */

import { useCallback, useEffect, useState } from 'react';

/**
 * useLocalStorage — Tuple API like useState but persisted
 *
 * @param key - localStorage key
 * @param initialValue - Fallback when key missing or JSON invalid
 */
export function useLocalStorage<T>(
  key: string,
  initialValue: T,
): [T, (value: T | ((prev: T) => T)) => void] {
  const readValue = useCallback((): T => {
    if (typeof window === 'undefined') return initialValue;
    try {
      const item = window.localStorage.getItem(key);
      return item ? (JSON.parse(item) as T) : initialValue;
    } catch {
      return initialValue;
    }
  }, [initialValue, key]);

  const [storedValue, setStoredValue] = useState<T>(readValue);

  const setValue = useCallback(
    (value: T | ((prev: T) => T)) => {
      setStoredValue((prev) => {
        const next = value instanceof Function ? value(prev) : value;
        window.localStorage.setItem(key, JSON.stringify(next));
        return next;
      });
    },
    [key],
  );

  useEffect(() => {
    setStoredValue(readValue());
  }, [readValue]);

  useEffect(() => {
    const onStorage = (e: StorageEvent) => {
      if (e.key === key && e.newValue) {
        setStoredValue(JSON.parse(e.newValue) as T);
      }
    };
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, [key]);

  return [storedValue, setValue];
}
