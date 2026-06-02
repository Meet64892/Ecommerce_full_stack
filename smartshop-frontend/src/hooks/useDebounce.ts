/**
 * useDebounce.ts — Generic debounce hook for any value type
 *
 * PURPOSE:
 * Coalesces rapid value changes (typing in search) into one stable value after a delay.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Debouncing: Wait until user pauses before acting (vs throttle: max once per interval).
 * - useEffect cleanup: Returning a function clears the pending timeout on re-render/unmount.
 * - Elevator analogy: Door won't close while people keep pressing the button.
 *
 * CONNECTED TO:
 * - ProductSearch, ProductListPage
 */

import { useEffect, useState } from 'react';

/**
 * useDebounce — Returns `value` only after it stops changing for `delay` ms
 *
 * LEARNING NOTE: Generic <T> preserves type of debounced value (string, number, object).
 *
 * @param value - Source value that changes frequently (e.g. search input)
 * @param delay - Milliseconds to wait after last change
 */
export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const timer = window.setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      window.clearTimeout(timer);
    };
  }, [value, delay]);

  return debouncedValue;
}
