# Multi-Vendor Marketplace — Roles & Workflows

This document maps your **Super Admin / Admin (Vendor) / Customer** specification to the SmartShop implementation.

## Role mapping

| Your spec | Backend `Role` enum | UI label |
|-----------|----------------------|----------|
| Super Admin (platform owner) | `SUPER_ADMIN` | Super Admin |
| Admin (brand / vendor) | `SUPER_USER` | Admin (Vendor) |
| User (customer) | `USER` | Customer |

## Hierarchy

```text
SUPER_ADMIN  →  full platform (brands, users, product approval, analytics)
SUPER_USER   →  own brand catalog & orders (scoped by brandId)
USER         →  browse, cart, checkout, profile
```

## Implemented workflows

### Vendor onboarding

1. Customer registers (`POST /auth/register` → `USER`).
2. Customer applies: `POST /brands/apply` → brand `PENDING`.
3. Super Admin: `PATCH /admin/brands/{id}/approve` → user becomes `SUPER_USER`, `brandId` set.
4. **Or** Super Admin: `PATCH /admin/users/{id}/role` with `SUPER_USER` → manual vendor admin promotion.
5. Vendor adds product: `POST /products` → `PENDING` approval.
6. Super Admin: `PATCH /admin/products/{id}/approve` → product visible in shop (`GET /products`).

### Super Admin role management

- `PATCH /admin/users/{id}/role` body: `{ "role": "USER" | "SUPER_USER" | "SUPER_ADMIN" }`
- UI: `/admin/users` — role dropdown per user
- Rules: cannot change own role; cannot remove last `SUPER_ADMIN`; promoting to `SUPER_USER` links an approved brand if one exists

### Customer purchase

Storefront: browse (public `GET /products`), cart, checkout, orders.

### Security

- JWT claims: `role`, `userId`, `brandId` (vendors).
- API Gateway propagates `X-User-*` headers; `/admin/**` requires `SUPER_ADMIN`.

## Default Super Admin (local dev)

- Email: `superadmin@smartshop.local`
- Password: `Admin123!`

## Frontend routes

| Route | Role |
|-------|------|
| `/admin` | Super Admin dashboard |
| `/admin/brands` | Approve vendor applications |
| `/admin/products` | Approve product listings |
| `/admin/users` | User list + role changes |
| `/vendor` | Vendor dashboard |
| `/vendor/apply` | Brand application (customers) |
| `/brand/products` | Vendor product CRUD |

---

## Feature status vs your full spec

### Super Admin

| Area | Status | Notes |
|------|--------|-------|
| Dashboard analytics (revenue, orders, users, brands) | **Partial** | Basic counts via `/admin/stats`; no revenue/commission charts |
| Brand approve/reject/suspend | **Done** | `/admin/brands` |
| Create vendor admin manually | **Done** | Role dropdown → `SUPER_USER` |
| Product approve/reject (all brands) | **Done** | `/admin/products` |
| Edit/delete any product | **Partial** | Backend CRUD exists; no super-admin product editor UI |
| Global categories/attributes | **Partial** | Categories API; no attribute/spec UI |
| Orders platform-wide | **Missing** | No admin orders view |
| Disputes, refunds, returns | **Missing** | |
| User suspend/activate | **Partial** | Brand suspend disables owner; no per-user toggle UI |
| Financial / commission / payouts | **Missing** | |
| CMS, banners, coupons, blog | **Missing** | |
| System settings (tax, shipping, payments, email) | **Missing** | |

### Admin (Vendor)

| Area | Status | Notes |
|------|--------|-------|
| Vendor dashboard metrics | **Partial** | `/vendor` stub metrics |
| Own product CRUD | **Done** | `/brand/products` |
| Variants (size/color/etc.) | **Missing** | |
| Inventory / low-stock alerts | **Partial** | Inventory service exists; no vendor UI |
| Own orders only | **Missing** | Orders not split by vendor |
| Coupons / flash sales | **Missing** | |
| Reviews reply | **Missing** | |

### Customer (User)

| Area | Status | Notes |
|------|--------|-------|
| Register / login | **Done** | |
| Browse / search / filter | **Done** | |
| Cart / checkout / orders | **Done** | Single-vendor checkout (no split cart) |
| Wishlist / compare | **Missing** | |
| Multiple addresses | **Missing** | |
| OTP / social login | **Missing** | |
| UPI / wallets / COD | **Missing** | Demo checkout only |
| Returns / refunds / invoices | **Missing** | |
| Reviews | **Missing** | |
| Support tickets / live chat | **Missing** | |

### Cross-cutting

| Area | Status |
|------|--------|
| Email/SMS/push notifications | **Partial** (Kafka email on register/order) |
| 2FA / audit logs | **Missing** |
| Multi-vendor order split | **Missing** |

---

## Suggested implementation order (next)

1. Admin orders list (platform + vendor-scoped)
2. User suspend/activate in `/admin/users`
3. Vendor order fulfillment (accept, ship, track)
4. Returns/refunds module
5. Coupons + commission/payouts
6. Wishlist, addresses, reviews (customer)
