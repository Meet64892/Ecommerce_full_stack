/**
 * Footer.tsx — Site footer
 */

import { Link } from 'react-router-dom';
import { ROUTES } from '@utils/constants';

export function Footer() {
  return (
    <footer className="mt-auto w-full border-t border-neutral-800 bg-neutral-950 py-8">
      <div className="w-full px-4 text-center text-sm text-neutral-500 sm:px-6">
        <p>
          © {new Date().getFullYear()} {import.meta.env.VITE_APP_NAME ?? 'SmartShop'} — Educational
          React storefront for microservices learning.
        </p>
        <p className="mt-2">
          <Link to={ROUTES.PRODUCTS} className="text-white hover:underline">
            Browse products
          </Link>
        </p>
      </div>
    </footer>
  );
}
