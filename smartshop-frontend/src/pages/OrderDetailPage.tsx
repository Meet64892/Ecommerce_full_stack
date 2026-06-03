/**
 * OrderDetailPage.tsx — Single order view with timeline
 */

import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { orderApi } from '@api/orderApi';
import { OrderTimeline } from '@components/order/OrderTimeline';
import { OrderSummary } from '@components/order/OrderSummary';
import { Spinner } from '@components/ui/Spinner';
import { formatOrderStatus, formatRelativeTime } from '@utils/formatters';

export default function OrderDetailPage() {
  const { id } = useParams<{ id: string }>();

  const { data: order, isLoading } = useQuery({
    queryKey: ['order', id],
    queryFn: () => orderApi.getById(id!),
    enabled: !!id,
  });

  if (isLoading) return <Spinner size="lg" />;
  if (!order) return <p>Order not found.</p>;

  return (
    <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="space-y-8">
      <div>
        <h1 className="font-display text-2xl font-bold text-slate-100">Order #{order.id.slice(0, 8)}</h1>
        <p className="text-sm text-slate-500">
          {formatOrderStatus(order.status)} · {formatRelativeTime(order.createdAt)}
        </p>
      </div>
      <OrderTimeline status={order.status} />
      <OrderSummary order={order} />
    </motion.div>
  );
}
