/**
 * order.types.ts — Order and checkout types
 *
 * PURPOSE:
 * Mirrors order-service DTOs and CreateOrderRequest for checkout.
 *
 * CONNECTED TO:
 * - src/api/orderApi.ts
 * - src/pages/CheckoutPage.tsx
 */

export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

/** Mirrors com.smartshop.order.dto.OrderItemDto */
export interface OrderItem {
  id: string;
  productId: string;
  quantity: number;
  unitPrice: number;
}

/** Mirrors com.smartshop.order.dto.OrderDto */
export interface Order {
  id: string;
  userId: string;
  status: OrderStatus;
  totalAmount: number;
  items: OrderItem[];
  createdAt: string;
}

export interface CreateOrderRequest {
  userId: string;
  items: Array<{
    productId: string;
    quantity: number;
    unitPrice: number;
  }>;
}

export interface ShippingAddress {
  fullName: string;
  address: string;
  city: string;
  state: string;
  pincode: string;
  phone: string;
}
