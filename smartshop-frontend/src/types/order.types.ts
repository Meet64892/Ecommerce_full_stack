/**
 * order.types.ts — Order and checkout types
 */

export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface OrderItem {
  id: string;
  productId: string;
  brandId?: string;
  productName?: string;
  quantity: number;
  unitPrice: number;
}

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
    brandId?: string;
    productName?: string;
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
