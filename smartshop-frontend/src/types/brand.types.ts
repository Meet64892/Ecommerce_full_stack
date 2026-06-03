export type BrandStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED';

export interface Brand {
  id: string;
  name: string;
  slug: string;
  description: string;
  logoUrl?: string;
  ownerUserId: string;
  status: BrandStatus;
  commissionPercent: number;
  createdAt: string;
}

export interface BrandApplyRequest {
  name: string;
  description: string;
  slug: string;
}

export interface CreateVendorAdminRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  brand: BrandApplyRequest;
  commissionPercent?: number;
}

export interface PlatformStats {
  totalCustomers: number;
  totalVendors: number;
  pendingBrandApplications: number;
  approvedBrands: number;
  suspendedBrands: number;
}
