# SmartShop — Enterprise E-Commerce Microservices Platform

SmartShop is a **production-grade, educational** Spring Boot microservices project.
Every file is heavily commented to explain *why* each decision was made, so you can
read the codebase like a textbook on microservice architecture.

- **Spring Boot** 3.2.3 · **Spring Cloud** 2023.0.0 · **Java** 21 · **Gradle** 8.6 (Kotlin DSL)

---

## 1. Architecture

```
                                   ┌───────────────────────────┐
                                   │          Clients          │
                                   │   (web / mobile / curl)   │
                                   └─────────────┬─────────────┘
                                                 │  HTTPS / JSON
                                                 ▼
                                   ┌───────────────────────────┐
                                   │     API Gateway  :8080    │
                                   │  (Spring Cloud Gateway)   │
                                   │  • JWT auth filter        │
                                   │  • Rate limiting (Redis)  │
                                   │  • Circuit breaker        │
                                   │  • Correlation-Id logging │
                                   └───┬─────────┬─────────┬───┘
                          routes by lb://service-id (via Eureka)
        ┌────────────────────┼─────────┼─────────┼────────────────────┐
        ▼                    ▼         ▼         ▼                    ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│ user-service  │  │product-service│  │ order-service │  │inventory-svc  │  │notification-  │
│    :8081      │  │    :8082      │  │    :8083      │  │   :8084       │  │  svc  :8085   │
│ Auth + JWT    │  │ Catalog +     │  │ Orders +      │  │ Stock +       │  │ Email via     │
│ PostgreSQL    │  │ Elasticsearch │  │ Saga          │  │ Redis cache   │  │ Kafka events  │
└──────┬────────┘  └──────┬────────┘  └──────┬────────┘  └──────┬────────┘  └──────┬────────┘
       │                  │                  │                  │                  │
       │ PostgreSQL       │ PG + ES          │ PostgreSQL       │ PG + Redis       │ (no DB)
       │                  │                  │                  │                  │
       │                  │        ┌─────────┴──────────────────┴─────────┐        │
       │                  │        │            Apache Kafka              │◄───────┘
       │                  │        │  order.created / inventory.checked   │
       │                  │        │  order.confirmed / order.cancelled   │
       │                  │        └──────────────────────────────────────┘
       │                  │
       ▼                  ▼
┌──────────────────────────────────────────────────────────────────────────────────────┐
│  Platform infrastructure                                                               │
│  • service-registry :8761 (Eureka)   — where services find each other                  │
│  • config-server    :8888            — centralized externalized configuration          │
│  • zipkin           :9411            — distributed tracing UI (B3 propagation)          │
│  • PostgreSQL :5432 · Redis :6379 · Elasticsearch :9200 · Kafka :9092                   │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

**Request lifecycle (example: place an order)**

1. Client `POST /api/orders` → **API Gateway** validates the JWT, stamps a
   correlation id, rate-limits, and forwards via `lb://order-service`.
2. **order-service** saves a `PENDING` order (local transaction) and publishes
   `order.created` to **Kafka** (the Saga begins).
3. **inventory-service** consumes `order.created`, reserves stock using
   optimistic locking, and replies `inventory.checked`.
4. **order-service** consumes the reply: on success it moves the order to
   `CONFIRMED` and emits `order.confirmed`; on failure it `CANCELLED`s and emits
   `order.cancelled` (compensation).
5. **notification-service** consumes the outcome and "emails" the customer.

Throughout, **Zipkin** stitches the spans into one trace via the `traceId` that
propagates across HTTP and Kafka boundaries.

---

## 2. What you'll learn

| Service / module       | Concepts demonstrated                                                                 |
|------------------------|---------------------------------------------------------------------------------------|
| `common-lib`           | Shared DTOs, `java-library` vs `spring-boot` plugin, DRY API envelope, base events    |
| `service-registry`     | Service discovery, Eureka heartbeats/lease/eviction, self-preservation                |
| `config-server`        | 12-factor config, `{application}/{profile}` resolution, fail-fast vs fail-safe        |
| `api-gateway`          | Reactive gateway, predicates/filters, JWT at the edge, Redis rate limiting, circuit breaker |
| `user-service`         | JWT (HS256 vs RS256), BCrypt, Spring Security 6 filter chain, MapStruct, Flyway, auditing |
| `product-service`      | Dual persistence (PostgreSQL + Elasticsearch), inverted index, `Pageable`/`Page`/`Slice` |
| `order-service`        | Saga orchestration, why 2PC doesn't scale, eventual consistency, Kafka, Resilience4j  |
| `inventory-service`    | Redis cache-aside + TTL, optimistic vs pessimistic locking, race conditions, custom health |
| `notification-service` | Async decoupling, consumer groups, dead-letter topics, Thymeleaf templates            |

---

## 3. Prerequisites

- **Java 21** (`java -version` should report 21)
- **Docker** + **Docker Compose** (for the infrastructure)
- No local Gradle needed — the **Gradle wrapper** (`./gradlew`) is included.

---

## 4. Setup — step by step

```bash
# 1) Start all infrastructure (Postgres, Redis, Elasticsearch, Kafka, Zipkin).
docker-compose up -d

# 2) Build the whole project (downloads dependencies the first time).
./gradlew build

# 3) Start the platform services FIRST (each in its own terminal):
./gradlew :service-registry:bootRun     # http://localhost:8761  (Eureka dashboard)
./gradlew :config-server:bootRun        # http://localhost:8888
./gradlew :api-gateway:bootRun          # http://localhost:8080

# 4) Start the business services (each in its own terminal):
./gradlew :user-service:bootRun         # http://localhost:8081
./gradlew :product-service:bootRun      # http://localhost:8082
./gradlew :order-service:bootRun        # http://localhost:8083
./gradlew :inventory-service:bootRun    # http://localhost:8084
./gradlew :notification-service:bootRun # http://localhost:8085
```

Each service exposes Swagger UI at `http://localhost:<port>/swagger-ui.html` and
health at `http://localhost:<port>/actuator/health`.

> **Tip:** activate the dev profile with `--args='--spring.profiles.active=dev'`,
> e.g. `./gradlew :user-service:bootRun --args='--spring.profiles.active=dev'`.

---

## 5. API examples (via the gateway on :8080)

```bash
# Register a user (public endpoint) — returns a JWT.
curl -s -X POST http://localhost:8080/api/users/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"ada@example.com","password":"secret12","fullName":"Ada Lovelace"}'

# Login — returns a JWT.
TOKEN=$(curl -s -X POST http://localhost:8080/api/users/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"ada@example.com","password":"secret12"}' | sed -E 's/.*"accessToken":"([^"]+)".*/\1/')

# Current user (protected — send the bearer token).
curl -s http://localhost:8080/api/users/auth/me -H "Authorization: Bearer $TOKEN"

# Create a category and a product.
curl -s -X POST "http://localhost:8080/api/products/categories?name=Electronics" \
  -H "Authorization: Bearer $TOKEN"
curl -s -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"name":"Wireless Mouse","description":"Ergonomic","price":29.99,"categoryId":1}'

# Search products (Elasticsearch).
curl -s "http://localhost:8080/api/products/search?query=wireless" \
  -H "Authorization: Bearer $TOKEN"

# Check inventory (product 1 is seeded with 100 units).
curl -s http://localhost:8080/api/inventory/1 -H "Authorization: Bearer $TOKEN"

# Place an order (kicks off the Saga; watch notification-service logs).
curl -s -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"userId":1,"items":[{"productId":1,"quantity":2,"unitPrice":29.99}]}'

# Ordering product 3 (seeded with 0 stock) demonstrates the CANCELLED path.
curl -s -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"userId":1,"items":[{"productId":3,"quantity":1,"unitPrice":9.99}]}'
```

---

## 6. Kafka topics

| Topic               | Producer            | Consumer(s)                       | Purpose                                            |
|---------------------|---------------------|-----------------------------------|----------------------------------------------------|
| `order.created`     | order-service       | inventory-service                 | Start the saga; request stock reservation          |
| `inventory.checked` | inventory-service   | order-service                     | Reply with reservation success/failure             |
| `order.confirmed`   | order-service       | notification-service              | Order succeeded → send confirmation email          |
| `order.cancelled`   | order-service       | notification-service              | Order failed (compensation) → send cancellation    |
| `user.registered`   | (user-service*)     | notification-service              | New account → send welcome email                   |

\* `user.registered` is consumed by notification-service; wiring user-service to
produce it is left as a guided exercise (the consumer is fully implemented).

Cross-service events use **logical type-token mappings**
(`spring.json.type.mapping`) so each service maps the token to its **own** event
class — no shared event jar, no package coupling.

---

## 7. Service ports

| Service                | Port  | Backing stores                  |
|------------------------|-------|---------------------------------|
| api-gateway            | 8080  | Redis (rate limiting)           |
| user-service           | 8081  | PostgreSQL `smartshop_users`    |
| product-service        | 8082  | PostgreSQL `smartshop_products` + Elasticsearch |
| order-service          | 8083  | PostgreSQL `smartshop_orders` + Kafka |
| inventory-service      | 8084  | PostgreSQL `smartshop_inventory` + Redis + Kafka |
| notification-service   | 8085  | Kafka (consumer only)           |
| service-registry       | 8761  | —                               |
| config-server          | 8888  | —                               |
| PostgreSQL             | 5432  | —                               |
| Redis                  | 6379  | —                               |
| Elasticsearch          | 9200  | —                               |
| Kafka                  | 9092  | —                               |
| Zipkin                 | 9411  | —                               |

---

## 8. Glossary of patterns used

- **Service Discovery (Eureka):** a registry where services register and look
  each other up by logical name instead of hard-coded host:port.
- **API Gateway:** single entry point that centralizes cross-cutting concerns
  (auth, rate limiting, logging, circuit breaking).
- **Centralized Configuration (12-Factor III):** config lives outside the
  artifact so the same build runs in any environment.
- **JWT:** stateless, self-verifying token (`header.payload.signature`).
  HS256 = symmetric secret; RS256 = asymmetric key pair.
- **DTO vs Entity:** API-facing data carriers decoupled from persistence models.
- **MapStruct:** compile-time, reflection-free entity↔DTO mapping.
- **Flyway:** versioned, replayable database migrations (never ALTER by hand).
- **Pageable / Page / Slice:** paging abstractions; `Page` adds a total count,
  `Slice` only knows if a next page exists.
- **Inverted Index (Elasticsearch):** term → documents map enabling fast
  full-text search; scaled via shards and replicated for availability.
- **Saga Pattern:** a sequence of local transactions coordinated by events, with
  compensating actions, replacing a non-scalable distributed (2PC) transaction.
- **Eventual Consistency:** the system converges to a consistent state over time
  rather than being strongly consistent at every instant.
- **Idempotency:** safely processing the same (at-least-once) message twice has
  no extra effect; achieved via event ids / state guards.
- **Optimistic vs Pessimistic Locking:** detect conflicts at write time via a
  `@Version` (optimistic) vs lock the row up front with `SELECT ... FOR UPDATE`
  (pessimistic).
- **Cache-aside (Redis):** read the cache first, load+populate on a miss; TTL and
  event-driven eviction keep it fresh.
- **Circuit Breaker:** state machine (CLOSED → OPEN → HALF_OPEN) that fails fast
  when a dependency is unhealthy and recovers gracefully.
- **Distributed Tracing (Zipkin / B3):** a `traceId` propagated across services
  ties all spans of one request together.
- **Liveness vs Readiness:** "is it alive?" (restart if not) vs "can it serve
  traffic now?" (remove from load balancer if not).

---

## 9. Project layout

```
smartshop/
├── build.gradle.kts         # root: shared config via subprojects {}
├── settings.gradle.kts      # registers all modules
├── gradle.properties        # centralized versions
├── docker-compose.yml       # all infrastructure
├── docker/postgres-init/    # creates per-service databases
├── common-lib/              # shared DTOs, exceptions, base events, utils
├── service-registry/        # Eureka server
├── config-server/           # Spring Cloud Config Server + config-repo/
├── api-gateway/             # Spring Cloud Gateway
├── user-service/            # Auth + JWT + users
├── product-service/         # Products + Elasticsearch
├── order-service/           # Orders + Saga
├── inventory-service/       # Stock + Redis cache
└── notification-service/    # Email via Kafka events
```

---

*Authored by the SmartShop Team as a learning reference. Read the inline comments
— every class explains its purpose, key concepts, and how it fits the system.*
