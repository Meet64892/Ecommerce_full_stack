/**
 * Footer.tsx — Site footer
 */

import { Link } from 'react-router-dom';
import { ROUTES } from '@utils/constants';

export function Footer() {
  return (
    <footer className="mt-auto border-t border-gray-200 bg-white py-8">
      <div className="mx-auto max-w-7xl px-4 text-center text-sm text-gray-500 sm:px-6 lg:px-8">
        <p>
          © {new Date().getFullYear()} {import.meta.env.VITE_APP_NAME ?? 'SmartShop'} — Educational
          React storefront for microservices learning.
        </p>
        <p className="mt-2">
          <Link to={ROUTES.PRODUCTS} className="text-primary-600 hover:underline">
            Browse products
          </Link>
        </p>
      </div>
    </footer>
  );
}
