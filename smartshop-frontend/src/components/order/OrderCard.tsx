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

export function OrderCard({ order }: { order: Order }) {
  const [expanded, setExpanded] = useState(false);

  return (
    <div className="rounded-xl border border-gray-200 bg-white overflow-hidden">
      <button
        type="button"
        className="flex w-full items-center justify-between p-4 text-left hover:bg-gray-50"
        onClick={() => setExpanded((e) => !e)}
      >
        <div>
          <p className="font-medium text-gray-900">Order #{order.id.slice(0, 8)}</p>
          <p className="text-sm text-gray-500">{formatRelativeTime(order.createdAt)}</p>
        </div>
        <div className="flex items-center gap-3">
          <Badge variant={order.status === 'DELIVERED' ? 'success' : 'gray'}>
            {formatOrderStatus(order.status)}
          </Badge>
          <span className="font-semibold">{formatCurrency(order.totalAmount)}</span>
        </div>
      </button>
      <AnimatePresence>
        {expanded && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            className="border-t px-4 pb-4"
          >
            <div className="py-4">
              <OrderTimeline status={order.status} />
            </div>
            <ul className="space-y-2 text-sm text-gray-600">
              {order.items.map((item) => (
                <li key={item.id} className="flex justify-between">
                  <span>
                    {`Product ${item.productId.slice(0, 8)}…`} × {item.quantity}
                  </span>
                  <span>{formatCurrency(item.unitPrice * item.quantity)}</span>
                </li>
              ))}
            </ul>
            <Link
              to={`/orders/${order.id}`}
              className="mt-3 inline-block text-sm text-primary-600 hover:underline"
            >
              View details
            </Link>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
