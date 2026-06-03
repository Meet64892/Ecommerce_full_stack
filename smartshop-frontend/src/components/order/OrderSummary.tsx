/**
 * OrderSummary.tsx — Order totals breakdown
 */

import type { Order } from '@/types/order.types';
import { formatCurrency } from '@utils/formatters';

export function OrderSummary({ order }: { order: Order }) {
  return (
    <div className="surface-card p-6">
      <h3 className="mb-4 font-semibold text-slate-100">Order summary</h3>
      <ul className="space-y-2 text-sm">
        {order.items.map((item) => (
          <li key={item.id} className="flex justify-between text-slate-400">
            <span>
              {item.productId.slice(0, 8)} × {item.quantity}
            </span>
            <span>{formatCurrency(item.unitPrice * item.quantity)}</span>
          </li>
        ))}
      </ul>
      <div className="mt-4 flex justify-between border-t border-slate-700/50 pt-4 font-semibold text-slate-100">
        <span>Total</span>
        <span className="gradient-text">{formatCurrency(order.totalAmount)}</span>
      </div>
    </div>
  );
}
