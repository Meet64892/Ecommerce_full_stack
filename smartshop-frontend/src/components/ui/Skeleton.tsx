/**
 * Skeleton.tsx — Placeholder loading UI (compound component pattern)
 */

import type { HTMLAttributes } from 'react';
import { cn } from '@utils/cn';

export type SkeletonProps = HTMLAttributes<HTMLDivElement>;

function SkeletonBase({ className, ...props }: SkeletonProps) {
  return (
    <div className={cn('animate-pulse rounded-md bg-neutral-800', className)} aria-hidden {...props} />
  );
}

function SkeletonText({ lines = 3 }: { lines?: number }) {
  return (
    <div className="space-y-2">
      {Array.from({ length: lines }).map((_, i) => (
        <SkeletonBase key={i} className={cn('h-3 w-full', i === lines - 1 && 'w-4/5')} />
      ))}
    </div>
  );
}

function SkeletonCircle({ size = 48 }: { size?: number }) {
  return <SkeletonBase className="rounded-full" style={{ width: size, height: size }} />;
}

export const Skeleton = Object.assign(SkeletonBase, {
  Text: SkeletonText,
  Circle: SkeletonCircle,
});
