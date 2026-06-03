/**
 * Footer.tsx — Site footer
 */

import { Link } from 'react-router-dom';
import { ROUTES } from '@utils/constants';

export function Footer() {
  return (
    <footer className="relative mt-auto w-full border-t border-slate-700/50 bg-surface/60 py-10 backdrop-blur-sm">
      <div
        className="pointer-events-none absolute inset-x-0 top-0 h-px bg-gradient-accent opacity-40"
        aria-hidden
      />
      <div className="w-full px-4 text-center text-sm text-slate-500 sm:px-6">
        <p>
          © {new Date().getFullYear()} {import.meta.env.VITE_APP_NAME ?? 'SmartShop'} — Educational
          React storefront for microservices learning.
        </p>
        <p className="mt-3">
          <Link
            to={ROUTES.PRODUCTS}
            className="link-accent font-medium"
          >
            Browse products
          </Link>
        </p>
      </div>
    </footer>
  );
}
