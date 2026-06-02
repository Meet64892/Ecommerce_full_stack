# SmartShop Frontend (Educational Edition)

Production-grade React + TypeScript storefront for the **SmartShop** Spring Boot microservices backend. Every layer is heavily commented so you can read the codebase like a course on modern React.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     Browser (React + Vite)                       │
│  axios → React Query → Pages/Components → Zustand (auth/cart) │
└────────────────────────────┬────────────────────────────────────┘
                             │  Dev: /api/* proxied → :8080
                             │  Prod: VITE_API_BASE_URL
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│              API Gateway (:8080) — Spring Cloud Gateway          │
├─────────────┬─────────────┬──────────────┬──────────────────────┤
│ user-service│product-svc  │ order-service│ inventory-service    │
│ /auth/**    │/products/** │ /orders/**   │ /inventory/**        │
│ /users/**   │/categories/**│             │                      │
└─────────────┴─────────────┴──────────────┴──────────────────────┘
```

## What you'll learn

| Area | Files | Concepts |
|------|-------|----------|
| Tooling | `vite.config.ts`, `.env.*` | HMR, path aliases, proxy vs CORS, `import.meta.env` |
| HTTP | `src/api/axiosInstance.ts` | Interceptors, JWT Bearer, error envelopes |
| Types | `src/types/*` | Generics, DTO mirroring, discriminated unions |
| Server state | `src/pages/ProductListPage.tsx` | `useInfiniteQuery`, URL search params, staleTime |
| Client state | `src/store/*` | Zustand, persist, immer |
| Forms | `src/pages/CheckoutPage.tsx` | react-hook-form, Zod, multi-step UI |
| Routing | `src/App.tsx` | lazy routes, nested layouts, protected routes |
| A11y UI | `src/components/ui/Modal.tsx` | Headless UI, focus trap, portals |
| Motion | `src/components/cart/CartDrawer.tsx` | Framer Motion, reduced motion |
| Testing | `src/**/*.test.ts(x)` | Vitest, Testing Library, `getByRole` |

## Tech stack

| Library | Role | Docs |
|---------|------|------|
| React 18 | UI | https://react.dev |
| Vite 5 | Bundler / dev server | https://vitejs.dev |
| TypeScript 5 | Types | https://www.typescriptlang.org |
| React Router 6 | Routing | https://reactrouter.com |
| TanStack Query 5 | Server cache | https://tanstack.com/query |
| Zustand 4 | Client global state | https://docs.pmnd.rs/zustand |
| Axios | HTTP | https://axios-http.com |
| Tailwind CSS 3 | Styling | https://tailwindcss.com |
| react-hook-form + Zod | Forms / validation | https://react-hook-form.com , https://zod.dev |
| Framer Motion 11 | Animation | https://www.framer.com/motion |
| Headless UI 2 | Accessible primitives | https://headlessui.com |
| Vitest + Testing Library | Unit tests | https://vitest.dev |

## Getting started

```bash
cd smartshop-frontend
npm install
cp .env.development .env.local   # optional — defaults work with proxy
npm run dev
```

Open http://localhost:5173. Ensure the backend API gateway runs on **http://localhost:8080** (see `/workspace/smartshop`).

```bash
npm run build    # production bundle
npm test         # unit tests
npm run lint     # ESLint
```

## Folder structure

- `src/api/` — HTTP clients; unwrap `ApiResponse<T>`
- `src/types/` — DTOs aligned with Java records
- `src/store/` — Zustand (auth, cart, UI)
- `src/hooks/` — Reusable React hooks
- `src/components/` — UI, layout, product, cart, order, auth
- `src/pages/` — Route-level screens (lazy-loaded)
- `src/utils/` — Formatters, validators, constants, errors
- `src/styles/` — Tailwind entry + custom animations

## Key patterns

### Data fetching

`axiosInstance` → `productApi.getAll()` → `useQuery` / `useInfiniteQuery` in pages → presentational components.

### Authentication

Login → JWT in `authStore` (persisted) → request interceptor adds `Authorization: Bearer` → `ProtectedRoute` guards checkout/orders.

### Cart

`ProductCard` → `cartStore` (persist + immer) → `CartDrawer` / `CartPage` → `CheckoutPage` → `POST /orders`.

### Forms

Zod schema → `@hookform/resolvers/zod` → `handleSubmit` → mutation → toast on error via `parseApiError`.

## Library decision log

- **Zustand over Redux** — Less boilerplate; no Provider; fine-grained subscriptions.
- **React Query over SWR** — Richer devtools, `useInfiniteQuery`, mutation helpers.
- **Zod over Yup** — TypeScript-first; infers form types from schema.
- **Vite over CRA** — Faster dev, ESM-native, official React template.
- **Tailwind over CSS Modules** — Utility-first; JIT purges unused CSS.
- **Vitest over Jest** — Same API; runs in Vite pipeline.

## Environment variables

| Variable | Development | Production |
|----------|-------------|------------|
| `VITE_API_BASE_URL` | `/api` (proxied) | `https://api.smartshop.com` |
| `VITE_APP_NAME` | SmartShop Dev | SmartShop |
| `VITE_ENABLE_DEVTOOLS` | `true` | `false` |

Never put secrets in `VITE_*` variables — they are embedded in the client bundle.

## License

Educational use alongside the SmartShop microservices repository.
