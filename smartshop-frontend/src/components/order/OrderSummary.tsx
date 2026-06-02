/**
 * OrderSummary.tsx — Order totals breakdown
 */

import type { Order } from '@/types/order.types';
import { formatCurrency } from '@utils/formatters';

export function OrderSummary({ order }: { order: Order }) {
  return (
    <div className="rounded-xl border border-gray-200 bg-white p-6">
      <h3 className="mb-4 font-semibold text-gray-900">Order summary</h3>
      <ul className="space-y-2 text-sm">
        {order.items.map((item) => (
          <li key={item.id} className="flex justify-between text-gray-600">
            <span>
              {item.productId.slice(0, 8)} × {item.quantity}
            </span>
            <span>{formatCurrency(item.unitPrice * item.quantity)}</span>
          </li>
        ))}
      </ul>
      <div className="mt-4 flex justify-between border-t pt-4 font-semibold">
        <span>Total</span>
        <span>{formatCurrency(order.totalAmount)}</span>
      </div>
    </div>
  );
}
