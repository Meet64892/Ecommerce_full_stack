/**
 * OrderCard.tsx — Expandable order summary
 */

import { useState } from 'react';
import { Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import type { Order } from '@/types/order.types';
import { formatCurrency, formatRelativeTime, formatOrderStatus } from '@utils/formatters';
import { OrderTimeline } from './OrderTimeline';
import { Badge } from '@components/ui/Badge';
import { cn } from '@utils/cn';

export function OrderCard({ order }: { order: Order }) {
  const [expanded, setExpanded] = useState(false);

  return (
    <div className="card-interactive overflow-hidden">
      <button
        type="button"
        className={cn(
          'flex w-full items-center justify-between p-4 text-left transition-colors duration-200',
          'hover:bg-surface-hover/50',
        )}
        onClick={() => setExpanded((e) => !e)}
      >
        <div>
          <p className="font-medium text-slate-100">Order #{order.id.slice(0, 8)}</p>
          <p className="text-sm text-slate-500">{formatRelativeTime(order.createdAt)}</p>
        </div>
        <div className="flex items-center gap-3">
          <Badge variant={order.status === 'DELIVERED' ? 'success' : 'gray'}>
            {formatOrderStatus(order.status)}
          </Badge>
          <span className="font-semibold text-slate-100">{formatCurrency(order.totalAmount)}</span>
        </div>
      </button>
      <AnimatePresence>
        {expanded && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.25, ease: [0.16, 1, 0.3, 1] }}
            className="border-t border-slate-700/50 px-4 pb-4"
          >
            <div className="py-4">
              <OrderTimeline status={order.status} />
            </div>
            <ul className="space-y-2 text-sm text-slate-400">
              {order.items.map((item) => (
                <li key={item.id} className="flex justify-between">
                  <span>
                    {`Product ${item.productId.slice(0, 8)}…`} × {item.quantity}
                  </span>
                  <span>{formatCurrency(item.unitPrice * item.quantity)}</span>
                </li>
              ))}
            </ul>
            <Link to={`/orders/${order.id}`} className="link-accent mt-3 inline-block text-sm">
              View details
            </Link>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
