/**
 * OrderTimeline.tsx — Visual PENDING → CONFIRMED → SHIPPED → DELIVERED progression
 */

import { motion } from 'framer-motion';
import { Check, Circle, Package, Truck } from 'lucide-react';
import type { OrderStatus } from '@/types/order.types';
import { ORDER_STATUS_LABELS } from '@utils/constants';
import { cn } from '@utils/cn';

const FLOW: OrderStatus[] = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED'];

const icons: Record<OrderStatus, typeof Circle> = {
  PENDING: Circle,
  CONFIRMED: Check,
  SHIPPED: Truck,
  DELIVERED: Package,
  CANCELLED: Circle,
};

const stepVariants = {
  inactive: { scale: 1 },
  active: { scale: [1, 1.15, 1], transition: { duration: 0.5 } },
  complete: { scale: 1 },
};

export function OrderTimeline({ status }: { status: OrderStatus }) {
  if (status === 'CANCELLED') {
    return <p className="text-sm text-red-400">Order was cancelled.</p>;
  }

  const currentIndex = FLOW.indexOf(status);

  return (
    <ol className="flex items-center justify-between">
      {FLOW.map((step, index) => {
        const isComplete = index < currentIndex;
        const isCurrent = index === currentIndex;
        const Icon = icons[step];
        return (
          <li key={step} className="flex flex-1 flex-col items-center">
            <div className="flex w-full items-center">
              {index > 0 && (
                <div
                  className={cn(
                    'h-0.5 flex-1 transition-colors duration-300',
                    isComplete || isCurrent ? 'bg-gradient-accent' : 'bg-slate-700',
                  )}
                />
              )}
              <motion.div
                variants={stepVariants}
                initial="inactive"
                animate={isCurrent ? 'active' : isComplete ? 'complete' : 'inactive'}
                className={cn(
                  'flex h-10 w-10 items-center justify-center rounded-full border-2 transition-all duration-300',
                  isComplete && 'border-primary-500 bg-gradient-accent text-white shadow-glow-sm',
                  isCurrent && 'border-primary-400 bg-surface-elevated text-primary-300 shadow-glow-sm',
                  !isComplete && !isCurrent && 'border-slate-700 bg-surface-muted text-slate-600',
                  isCurrent && 'animate-pulse-glow',
                )}
              >
                <Icon className="h-5 w-5" />
              </motion.div>
              {index < FLOW.length - 1 && (
                <div
                  className={cn(
                    'h-0.5 flex-1 transition-colors duration-300',
                    index < currentIndex ? 'bg-gradient-accent' : 'bg-slate-700',
                  )}
                />
              )}
            </div>
            <span
              className={cn(
                'mt-2 text-xs font-medium transition-colors',
                isCurrent ? 'text-primary-300' : isComplete ? 'text-slate-300' : 'text-slate-600',
              )}
            >
              {ORDER_STATUS_LABELS[step]}
            </span>
          </li>
        );
      })}
    </ol>
  );
}
