import { DashboardLayout } from '@components/layout/DashboardLayout';
import { ROUTES } from '@utils/constants';

const nav = [
  { to: ROUTES.ADMIN, label: 'Overview' },
  { to: ROUTES.ADMIN_BRANDS, label: 'Brand applications' },
  { to: ROUTES.ADMIN_PRODUCTS, label: 'Product approval' },
  { to: ROUTES.ADMIN_USERS, label: 'Users' },
];

export default function AdminDashboardPage() {
  return <DashboardLayout title="Super Admin" items={nav} />;
}
