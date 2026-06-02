/**
 * useIntersectionObserver.ts — Detects when an element enters the viewport
 *
 * PURPOSE:
 * Powers infinite scroll on ProductListPage without scroll event listeners.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - IntersectionObserver: Browser API; callback when target intersects root.
 * - Why not scroll listeners: Fire on every pixel → layout thrashing and jank.
 *
 * CONNECTED TO:
 * - ProductListPage (load more sentinel)
 */

import { useEffect, useState, type RefObject } from 'react';

/**
 * useIntersectionObserver — Returns true when ref element is visible
 *
 * @param elementRef - Ref attached to sentinel div at list bottom
 * @param options - rootMargin, threshold (see MDN IntersectionObserverInit)
 */
export function useIntersectionObserver(
  elementRef: RefObject<Element | null>,
  options?: IntersectionObserverInit,
): boolean {
  const [isIntersecting, setIsIntersecting] = useState(false);

  useEffect(() => {
    const element = elementRef.current;
    if (!element) return;

    const observer = new IntersectionObserver(([entry]) => {
      setIsIntersecting(entry.isIntersecting);
    }, options);

    observer.observe(element);
    return () => observer.disconnect();
  }, [elementRef, options]);

  return isIntersecting;
}
