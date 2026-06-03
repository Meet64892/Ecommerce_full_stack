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
import { Input } from '@components/ui/Input';
import { Button } from '@components/ui/Button';
import { formatCurrency } from '@utils/formatters';
import { parseApiError } from '@utils/errorHandler';
import toast from 'react-hot-toast';
import { CheckCircle } from 'lucide-react';

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
    if (!isAuthenticated) {
      navigate('/login?redirect=/checkout');
    }
  }, [isAuthenticated, navigate]);

  if (!isAuthenticated) {
    return null;
  }

  if (items.length === 0 && !orderId) {
    return <p className="text-gray-500">Your cart is empty.</p>;
  }

  const onPlaceOrder = async () => {
    if (!user) return;
    const order = await createOrder.mutateAsync({
      userId: user.id,
      items: items.map((i) => ({
        productId: i.product.id,
        quantity: i.quantity,
        unitPrice: i.product.price,
        brandId: i.product.brandId,
        productName: i.product.name,
      })),
    });
    clearCart();
    setOrderId(order.id);
    setStep(3);
    toast.success('Order placed!');
  };

  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="mx-auto max-w-2xl">
      <h1 className="mb-6 text-2xl font-bold">Checkout</h1>

      <ol className="mb-8 flex justify-between">
        {STEPS.map((label, i) => (
          <li
            key={label}
            className={`flex-1 text-center text-sm font-medium ${
              i <= step ? 'text-primary-600' : 'text-gray-400'
            }`}
          >
            <span
              className={`mx-auto mb-1 flex h-8 w-8 items-center justify-center rounded-full text-xs ${
                i <= step ? 'bg-primary-600 text-white' : 'bg-gray-200'
              }`}
            >
              {i + 1}
            </span>
            {label}
          </li>
        ))}
      </ol>

      <AnimatePresence mode="wait">
        {step === 0 && (
          <motion.div key="cart" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0 }}>
            <ul className="space-y-2 rounded-xl border bg-white p-4">
              {items.map((i) => (
                <li key={i.product.id} className="flex justify-between text-sm">
                  <span>
                    {i.product.name} × {i.quantity}
                  </span>
                  <span>{formatCurrency(i.product.price * i.quantity)}</span>
                </li>
              ))}
            </ul>
            <p className="mt-4 font-semibold">Total: {formatCurrency(totalPrice)}</p>
            <Button className="mt-4" onClick={() => setStep(1)}>
              Continue to shipping
            </Button>
          </motion.div>
        )}

        {step === 1 && (
          <motion.form
            key="shipping"
            onSubmit={handleSubmit(() => setStep(2))}
            className="space-y-4 rounded-xl border bg-white p-6"
          >
            <Input label="Full name" error={errors.fullName?.message} {...register('fullName')} />
            <Input label="Address" error={errors.address?.message} {...register('address')} />
            <div className="grid grid-cols-2 gap-4">
              <Input label="City" error={errors.city?.message} {...register('city')} />
              <Input label="State" error={errors.state?.message} {...register('state')} />
            </div>
            <Input label="Pincode" error={errors.pincode?.message} {...register('pincode')} />
            <Input label="Phone" error={errors.phone?.message} {...register('phone')} />
            <div className="flex gap-2">
              <Button type="button" variant="secondary" onClick={() => setStep(0)}>
                Back
              </Button>
              <Button type="submit">Continue to payment</Button>
            </div>
          </motion.form>
        )}

        {step === 2 && (
          <motion.div key="payment" className="rounded-xl border bg-white p-6">
            <p className="text-sm text-gray-600">
              Demo payment — no Stripe charge. Shipping to {getValues('fullName')},{' '}
              {getValues('city')}.
            </p>
            <div className="mt-4 flex gap-2">
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
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="text-center"
          >
            <CheckCircle className="mx-auto h-16 w-16 text-emerald-400 drop-shadow-[0_0_12px_rgba(52,211,153,0.5)]" />
            <h2 className="mt-4 text-xl font-bold text-slate-100">Order confirmed!</h2>
            <p className="mt-2 text-slate-400">Order ID: {orderId}</p>
            <Button className="mt-6" onClick={() => navigate(`/orders/${orderId}`)}>
              View order
            </Button>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
}
