/**
 * useClickOutside.ts — Calls handler when user clicks outside ref element
 *
 * PURPOSE:
 * Close dropdowns, modals built without Headless UI, mobile menus.
 *
 * CONNECTED TO:
 * - Custom dropdowns
 */

import { useEffect, type RefObject } from 'react';

export function useClickOutside<T extends HTMLElement>(
  ref: RefObject<T | null>,
  handler: () => void,
): void {
  useEffect(() => {
    const listener = (event: MouseEvent | TouchEvent) => {
      const el = ref.current;
      if (!el || el.contains(event.target as Node)) return;
      handler();
    };
    document.addEventListener('mousedown', listener);
    document.addEventListener('touchstart', listener);
    return () => {
      document.removeEventListener('mousedown', listener);
      document.removeEventListener('touchstart', listener);
    };
  }, [ref, handler]);
}
