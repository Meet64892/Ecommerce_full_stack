import { SUPER_ADMIN_ROUTES, VENDOR_ROUTES } from '@utils/constants';
import {
  LayoutDashboard,
  Package,
  ShoppingBag,
  Store,
  Users,
  Settings,
  Tag,
  BarChart3,
} from 'lucide-react';

export interface NavItem {
  to: string;
  label: string;
  icon: typeof LayoutDashboard;
}

export const SUPER_ADMIN_NAV: NavItem[] = [
  { to: SUPER_ADMIN_ROUTES.DASHBOARD, label: 'Dashboard', icon: LayoutDashboard },
  { to: SUPER_ADMIN_ROUTES.BRANDS, label: 'Brands / Vendors', icon: Store },
  { to: SUPER_ADMIN_ROUTES.PRODUCTS, label: 'Products', icon: Package },
  { to: SUPER_ADMIN_ROUTES.ORDERS, label: 'Orders', icon: ShoppingBag },
  { to: SUPER_ADMIN_ROUTES.USERS, label: 'Customers', icon: Users },
  { to: SUPER_ADMIN_ROUTES.SETTINGS, label: 'Settings', icon: Settings },
];

export const VENDOR_ADMIN_NAV: NavItem[] = [
  { to: VENDOR_ROUTES.DASHBOARD, label: 'Dashboard', icon: LayoutDashboard },
  { to: VENDOR_ROUTES.PRODUCTS, label: 'Products', icon: Package },
  { to: VENDOR_ROUTES.ORDERS, label: 'Orders', icon: ShoppingBag },
  { to: VENDOR_ROUTES.PROMOTIONS, label: 'Promotions', icon: Tag },
  { to: VENDOR_ROUTES.REPORTS, label: 'Reports', icon: BarChart3 },
];
