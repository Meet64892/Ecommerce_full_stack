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
    return <p className="text-sm text-red-600">Order was cancelled.</p>;
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
                  className={cn('h-0.5 flex-1', isComplete || isCurrent ? 'bg-primary-600' : 'bg-gray-200')}
                />
              )}
              <motion.div
                variants={stepVariants}
                initial="inactive"
                animate={isCurrent ? 'active' : isComplete ? 'complete' : 'inactive'}
                className={cn(
                  'flex h-10 w-10 items-center justify-center rounded-full border-2',
                  isComplete && 'border-primary-600 bg-primary-600 text-white',
                  isCurrent && 'border-primary-600 bg-white text-primary-600',
                  !isComplete && !isCurrent && 'border-gray-200 bg-white text-gray-300',
                  isCurrent && 'animate-pulse',
                )}
              >
                <Icon className="h-5 w-5" />
              </motion.div>
              {index < FLOW.length - 1 && (
                <div
                  className={cn('h-0.5 flex-1', index < currentIndex ? 'bg-primary-600' : 'bg-gray-200')}
                />
              )}
            </div>
            <span className="mt-2 text-xs font-medium text-gray-600">
              {ORDER_STATUS_LABELS[step]}
            </span>
          </li>
        );
      })}
    </ol>
  );
}
