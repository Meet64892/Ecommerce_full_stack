/**
 * ErrorFallback.tsx — UI shown when react-error-boundary catches a render error
 */

import { Button } from '@components/ui/Button';

export function ErrorFallback({
  error,
  resetErrorBoundary,
}: {
  error: Error;
  resetErrorBoundary: () => void;
}) {
  return (
    <div className="border border-red-900 bg-red-950/50 p-6 text-center">
      <h2 className="text-lg font-semibold text-red-300">Something went wrong</h2>
      <p className="mt-2 text-sm text-red-400">{error.message}</p>
      <Button className="mt-4" onClick={resetErrorBoundary}>
        Try again
      </Button>
    </div>
  );
}
