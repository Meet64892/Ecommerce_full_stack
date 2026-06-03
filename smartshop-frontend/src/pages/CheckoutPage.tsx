/**
 * CheckoutPage.tsx — Multi-step checkout with react-hook-form + zod
 */

import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation } from '@tanstack/react-query';
import { motion, AnimatePresence } from 'framer-motion';
import { orderApi } from '@api/orderApi';
import { useCart } from '@hooks/useCart';
import { useAuth } from '@hooks/useAuth';
import { useUiStore } from '@store/uiStore';
import { Input } from '@components/ui/Input';
import { Button } from '@components/ui/Button';
import { formatCurrency } from '@utils/formatters';
import { parseApiError } from '@utils/errorHandler';
import { ROUTES } from '@utils/constants';
import toast from 'react-hot-toast';
import { CheckCircle, ShoppingBag } from 'lucide-react';
import { cn } from '@utils/cn';

const shippingSchema = z.object({
  fullName: z.string().min(2, 'Name must be at least 2 characters'),
  address: z.string().min(5),
  city: z.string().min(2),
  state: z.string().min(2),
  pincode: z.string().regex(/^\d{6}$/, 'Invalid Indian pincode'),
  phone: z.string().regex(/^[6-9]\d{9}$/, 'Invalid Indian mobile number'),
});

type ShippingForm = z.infer<typeof shippingSchema>;

const STEPS = ['Cart', 'Shipping', 'Payment', 'Confirm'] as const;

export default function CheckoutPage() {
  const [step, setStep] = useState(0);
  const [orderId, setOrderId] = useState<string | null>(null);
  const { items, totalPrice, clearCart } = useCart();
  const { user, isAuthenticated } = useAuth();
  const setCartDrawerOpen = useUiStore((s) => s.setCartDrawerOpen);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
    getValues,
  } = useForm<ShippingForm>({ resolver: zodResolver(shippingSchema) });

  const createOrder = useMutation({
    mutationFn: orderApi.create,
    onError: (err) => toast.error(parseApiError(err)),
  });

  useEffect(() => {
    setCartDrawerOpen(false);
  }, [setCartDrawerOpen]);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login?redirect=/checkout');
    }
  }, [isAuthenticated, navigate]);

  if (!isAuthenticated) {
    return null;
  }

  if (items.length === 0 && !orderId) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        className="mx-auto max-w-lg text-center"
      >
        <ShoppingBag className="mx-auto h-12 w-12 text-slate-600" />
        <p className="mt-4 text-slate-400">Your cart is empty.</p>
        <Button className="mt-6" onClick={() => navigate(ROUTES.PRODUCTS)}>
          Continue shopping
        </Button>
      </motion.div>
    );
  }

  const onPlaceOrder = async () => {
    if (!user) return;
    const order = await createOrder.mutateAsync({
      userId: user.id,
      items: items.map((i) => ({
        productId: i.product.id,
        quantity: i.quantity,
        unitPrice: i.product.price,
      })),
    });
    clearCart();
    setCartDrawerOpen(false);
    setOrderId(order.id);
    setStep(3);
    toast.success('Order placed!');
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      className="mx-auto max-w-3xl px-4 sm:px-0"
    >
      <h1 className="mb-2 font-display text-2xl font-bold gradient-text">Checkout</h1>
      <p className="mb-8 text-sm text-slate-400">Complete your order in a few steps</p>

      <ol className="mb-8 flex gap-2 sm:gap-4">
        {STEPS.map((label, i) => (
          <li key={label} className="flex flex-1 flex-col items-center gap-2">
            <span
              className={cn(
                'flex h-9 w-9 items-center justify-center rounded-full text-xs font-semibold transition-colors',
                i < step && 'bg-emerald-500/20 text-emerald-400 ring-1 ring-emerald-500/40',
                i === step && 'bg-gradient-accent text-white shadow-glow-sm',
                i > step && 'bg-slate-800 text-slate-500 ring-1 ring-slate-700',
              )}
            >
              {i < step ? '✓' : i + 1}
            </span>
            <span
              className={cn(
                'text-center text-xs font-medium sm:text-sm',
                i <= step ? 'text-slate-200' : 'text-slate-500',
              )}
            >
              {label}
            </span>
          </li>
        ))}
      </ol>

      <AnimatePresence mode="wait">
        {step === 0 && (
          <motion.div
            key="cart"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            className="surface-card p-6"
          >
            <h2 className="mb-4 text-lg font-semibold text-slate-100">Order summary</h2>
            <ul className="divide-y divide-slate-700/50">
              {items.map((i) => (
                <li key={i.product.id} className="flex justify-between gap-4 py-3 text-sm">
                  <span className="text-slate-300">
                    {i.product.name}{' '}
                    <span className="text-slate-500">× {i.quantity}</span>
                  </span>
                  <span className="shrink-0 font-medium text-slate-100">
                    {formatCurrency(i.product.price * i.quantity)}
                  </span>
                </li>
              ))}
            </ul>
            <div className="mt-4 flex justify-between border-t border-slate-700/50 pt-4">
              <span className="font-medium text-slate-400">Total</span>
              <span className="text-lg font-bold gradient-text">{formatCurrency(totalPrice)}</span>
            </div>
            <Button className="mt-6 w-full sm:w-auto" onClick={() => setStep(1)}>
              Continue to shipping
            </Button>
          </motion.div>
        )}

        {step === 1 && (
          <motion.form
            key="shipping"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            onSubmit={handleSubmit(() => setStep(2))}
            className="surface-card space-y-4 p-6"
          >
            <h2 className="mb-2 text-lg font-semibold text-slate-100">Shipping details</h2>
            <Input label="Full name" error={errors.fullName?.message} {...register('fullName')} />
            <Input label="Address" error={errors.address?.message} {...register('address')} />
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <Input label="City" error={errors.city?.message} {...register('city')} />
              <Input label="State" error={errors.state?.message} {...register('state')} />
            </div>
            <Input label="Pincode" error={errors.pincode?.message} {...register('pincode')} />
            <Input label="Phone" error={errors.phone?.message} {...register('phone')} />
            <div className="flex flex-col gap-2 pt-2 sm:flex-row">
              <Button type="button" variant="secondary" onClick={() => setStep(0)}>
                Back
              </Button>
              <Button type="submit">Continue to payment</Button>
            </div>
          </motion.form>
        )}

        {step === 2 && (
          <motion.div
            key="payment"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -20 }}
            className="surface-card p-6"
          >
            <h2 className="mb-2 text-lg font-semibold text-slate-100">Payment</h2>
            <p className="text-sm text-slate-400">
              Demo payment — no charge will be made. Order ships to{' '}
              <span className="text-slate-200">
                {getValues('fullName')}, {getValues('city')}
              </span>
              .
            </p>
            <div className="mt-4 flex justify-between rounded-lg border border-slate-700/50 bg-slate-900/50 px-4 py-3">
              <span className="text-slate-400">Amount due</span>
              <span className="font-semibold gradient-text">{formatCurrency(totalPrice)}</span>
            </div>
            <div className="mt-6 flex flex-col gap-2 sm:flex-row">
              <Button variant="secondary" onClick={() => setStep(1)}>
                Back
              </Button>
              <Button isLoading={createOrder.isPending} onClick={() => void onPlaceOrder()}>
                Place order
              </Button>
            </div>
          </motion.div>
        )}

        {step === 3 && orderId && (
          <motion.div
            key="done"
            initial={{ scale: 0.95, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="surface-card p-8 text-center"
          >
            <CheckCircle className="mx-auto h-16 w-16 text-emerald-400 drop-shadow-[0_0_12px_rgba(52,211,153,0.5)]" />
            <h2 className="mt-4 text-xl font-bold text-slate-100">Order confirmed!</h2>
            <p className="mt-2 text-slate-400">
              Thank you for your purchase. Order ID:{' '}
              <span className="font-mono text-slate-300">{orderId}</span>
            </p>
            <div className="mt-6 flex flex-col justify-center gap-2 sm:flex-row">
              <Button onClick={() => navigate(`/orders/${orderId}`)}>View order</Button>
              <Button variant="secondary" onClick={() => navigate(ROUTES.PRODUCTS)}>
                Continue shopping
              </Button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
}
