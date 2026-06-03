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
4. Vendor adds product: `POST /products` → `PENDING` approval.
5. Super Admin: `PATCH /admin/products/{id}/approve` → product visible in shop (`GET /products`).

### Customer purchase

Unchanged storefront flow: browse (public `GET /products`), cart, checkout, orders.

### Security

- JWT claims: `role`, `userId`, `brandId` (vendors).
- API Gateway propagates `X-User-*` headers to services.
- `/admin/**` (except product routes on product-service) requires `SUPER_ADMIN` at the gateway.
- Product writes enforce vendor brand scope in `product-service`.

## Default Super Admin (local dev)

- Email: `superadmin@smartshop.local`
- Password: `Admin123!`

## Frontend routes

| Route | Role |
|-------|------|
| `/admin` | Super Admin dashboard |
| `/admin/brands` | Approve vendor applications |
| `/admin/products` | Approve product listings |
| `/admin/users` | User list |
| `/vendor` | Vendor dashboard |
| `/vendor/apply` | Brand application (customers) |
| `/brand/products` | Vendor product CRUD |

## Planned / stubbed (from your prompt)

Coupons, payouts, returns/refunds UI, CMS, social login, OTP, multi-vendor split checkout, notifications UI, and full financial reports are **not** fully implemented yet; the architecture supports adding them as separate services or modules.
