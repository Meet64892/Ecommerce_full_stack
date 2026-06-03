/**
 * CartPage.tsx — Full cart view
 */

import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useCart } from '@hooks/useCart';
import { CartItem } from '@components/cart/CartItem';
import { CartSummary } from '@components/cart/CartSummary';
import { ROUTES } from '@utils/constants';

export default function CartPage() {
  const { items, totalItems, totalPrice } = useCart();

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      className="mx-auto max-w-3xl"
    >
      <h1 className="mb-6 font-display text-2xl font-bold text-slate-100">Your cart</h1>
      {items.length === 0 ? (
        <p className="text-slate-500">
          Cart is empty.{' '}
          <Link to={ROUTES.PRODUCTS} className="link-accent">
            Continue shopping
          </Link>
        </p>
      ) : (
        <>
          <div className="surface-card px-4">
            {items.map((item) => (
              <CartItem key={item.product.id} item={item} />
            ))}
          </div>
          <div className="mt-6">
            <CartSummary totalItems={totalItems} totalPrice={totalPrice} />
          </div>
        </>
      )}
    </motion.div>
  );
}
