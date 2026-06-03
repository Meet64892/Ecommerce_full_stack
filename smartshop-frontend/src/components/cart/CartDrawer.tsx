/**
 * CartDrawer.tsx — Slide-in cart panel (Framer Motion x: 100%)
 *
 * Mobile-first: full width on small screens, 400px panel on md+.
 */


import { Link } from 'react-router-dom';
import { AnimatePresence, motion, useReducedMotion } from 'framer-motion';
import { X } from 'lucide-react';
import { useUiStore } from '@store/uiStore';
import { useCart } from '@hooks/useCart';
import { CartItem } from './CartItem';
import { CartSummary } from './CartSummary';
import { ROUTES } from '@utils/constants';

export function CartDrawer() {
  const open = useUiStore((s) => s.cartDrawerOpen);
  const setOpen = useUiStore((s) => s.setCartDrawerOpen);
  const { items, totalItems, totalPrice } = useCart();
  const reduceMotion = useReducedMotion();

  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-50 bg-black/40"
            onClick={() => setOpen(false)}
          />
          <motion.aside
            initial={reduceMotion ? false : { x: '100%' }}
            animate={{ x: 0 }}
            exit={reduceMotion ? undefined : { x: '100%' }}
            transition={{ type: 'spring', damping: 28, stiffness: 300 }}
            className="fixed inset-y-0 right-0 z-50 flex w-full max-w-full flex-col bg-white shadow-xl md:max-w-[400px]"
          >
            <div className="flex items-center justify-between border-b px-4 py-4">
              <h2 className="text-lg font-semibold">Your cart</h2>
              <button type="button" onClick={() => setOpen(false)} aria-label="Close cart">
                <X className="h-6 w-6" />
              </button>
            </div>
            <div className="flex-1 overflow-y-auto px-4">
              {items.length === 0 ? (
                <p className="py-8 text-center text-neutral-500">
                  Your cart is empty.{' '}
                  <Link to={ROUTES.PRODUCTS} className="text-white underline" onClick={() => setOpen(false)}>
                    Start shopping
                  </Link>
                </p>
              ) : (
                items.map((item) => <CartItem key={item.product.id} item={item} />)
              )}
            </div>
            {items.length > 0 && (
              <div className="border-t p-4">
                <CartSummary totalItems={totalItems} totalPrice={totalPrice} />
              </div>
            )}
          </motion.aside>
        </>
      )}
    </AnimatePresence>
  );
}
