/**
 * useMediaQuery.ts — Subscribes to CSS media query matches
 *
 * PURPOSE:
 * Responsive behavior in JS when Tailwind breakpoints aren't enough (e.g. conditional logic).
 *
 * CONNECTED TO:
 * - Layout, Header
 */

import { useEffect, useState } from 'react';

/**
 * useMediaQuery — Returns whether the query currently matches
 *
 * @param query - e.g. '(min-width: 768px)'
 */
export function useMediaQuery(query: string): boolean {
  const [matches, setMatches] = useState(() =>
    typeof window !== 'undefined' ? window.matchMedia(query).matches : false,
  );

  useEffect(() => {
    const media = window.matchMedia(query);
    const listener = () => setMatches(media.matches);
    listener();
    media.addEventListener('change', listener);
    return () => media.removeEventListener('change', listener);
  }, [query]);

  return matches;
}
