/**
 * useDebounce.test.ts — Debounce hook unit tests (Vitest fake timers)
 */

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useDebounce } from './useDebounce';

describe('useDebounce', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('returns initial value immediately', () => {
    const { result } = renderHook(() => useDebounce('hello', 400));
    expect(result.current).toBe('hello');
  });

  it('updates value only after delay', () => {
    const { result, rerender } = renderHook(
      ({ value, delay }) => useDebounce(value, delay),
      { initialProps: { value: 'a', delay: 400 } },
    );

    rerender({ value: 'ab', delay: 400 });
    expect(result.current).toBe('a');

    act(() => {
      vi.advanceTimersByTime(400);
    });
    expect(result.current).toBe('ab');
  });
});
