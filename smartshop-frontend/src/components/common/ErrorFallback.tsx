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
    <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center">
      <h2 className="text-lg font-semibold text-red-800">Something went wrong</h2>
      <p className="mt-2 text-sm text-red-600">{error.message}</p>
      <Button className="mt-4" onClick={resetErrorBoundary}>
        Try again
      </Button>
    </div>
  );
}
