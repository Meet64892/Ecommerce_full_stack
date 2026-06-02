# SmartShop — Enterprise E-Commerce Microservices Platform

A production-grade, educational microservices platform built with Spring Boot 3.2, Spring Cloud, and modern Java 21.
Every file is heavily commented to explain **why** each decision was made.

---

## Architecture Overview

```
                                  ┌─────────────────────────────────────────────────────────────┐
                                  │                    SMARTSHOP PLATFORM                        │
                                  │                                                               │
  ┌──────────────────┐            │  ┌──────────────────────────────────────────────────────┐    │
  │   Web Browser    │            │  │              INFRASTRUCTURE SERVICES                  │    │
  │   Mobile App     │            │  │  ┌─────────────────────┐  ┌─────────────────────┐   │    │
  │   External API   │            │  │  │   service-registry   │  │    config-server    │   │    │
  └────────┬─────────┘            │  │  │  (Eureka) :8761      │  │  (Spring Config)    │   │    │
           │ HTTP                 │  │  │  Service discovery   │  │  :8888              │   │    │
           │                      │  │  └─────────────────────┘  └─────────────────────┘   │    │
           ▼                      │  └──────────────────────────────────────────────────────┘    │
  ┌──────────────────┐            │                                                               │
  │   api-gateway    │            │  ┌──────────────────────────────────────────────────────┐    │
  │   :8080          │◄──────────►│  │                BUSINESS SERVICES                     │    │
  │  - JWT Auth      │  routes    │  │  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │    │
  │  - Rate Limiting │            │  │  │ user-service  │  │product-serv  │  │order-serv  │ │    │
  │  - Circuit Brk   │            │  │  │   :8081       │  │   :8082      │  │  :8083     │ │    │
  │  - Correlation ID│            │  │  │  Auth + JWT   │  │ Products +   │  │ Orders +   │ │    │
  └──────────────────┘            │  │  │  Users (PG)   │  │  Elastic (PG)│  │ Saga (PG)  │ │    │
                                  │  │  └──────────────┘  └──────────────┘  └────────────┘ │    │
                                  │  │  ┌──────────────┐  ┌──────────────────────────────┐ │    │
                                  │  │  │inventory-svc  │  │   notification-service       │ │    │
                                  │  │  │   :8084       │  │          :8085               │ │    │
                                  │  │  │  Stock + Redis│  │  Kafka consumer only         │ │    │
                                  │  │  │  Opt. Locking │  │  Email via Thymeleaf        │ │    │
                                  │  │  └──────────────┘  └──────────────────────────────┘ │    │
                                  │  └──────────────────────────────────────────────────────┘    │
                                  │                                                               │
                                  │  ┌──────────────────────────────────────────────────────┐    │
                                  │  │              MESSAGING & EVENTS (KAFKA)               │    │
                                  │  │    order.created ──► inventory-service, notification  │    │
                                  │  │    order.confirmed ──► notification-service           │    │
                                  │  │    order.cancelled ──► inventory, notification        │    │
                                  │  │    inventory.checked ──► order-service (Saga reply)   │    │
                                  │  └──────────────────────────────────────────────────────┘    │
                                  │                                                               │
                                  │  ┌──────────────────────────────────────────────────────┐    │
                                  │  │              DATA STORES                              │    │
                                  │  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐ │    │
                                  │  │  │PostgreSQL│ │  Redis   │ │Elasticsearch│ │Zipkin │ │    │
                                  │  │  │  :5432   │ │  :6379   │ │  :9200    │ │ :9411 │ │    │
                                  │  │  │4 databases│ │  Cache  │ │  Products │ │Traces │ │    │
                                  │  │  └──────────┘ └──────────┘ └──────────┘ └────────┘ │    │
                                  │  └──────────────────────────────────────────────────────┘    │
                                  └─────────────────────────────────────────────────────────────┘
```

---

## What You'll Learn

| Service | Core Spring/Java Concepts |
|---------|--------------------------|
| **service-registry** | Service Discovery, Eureka, Heartbeat, Lease Renewal |
| **config-server** | 12-Factor App Config, Spring Cloud Config, Native/Git backends |
| **api-gateway** | Spring Cloud Gateway, Reactive/WebFlux, JWT Validation, Rate Limiting (Redis), Circuit Breaker (Resilience4j), Correlation IDs |
| **user-service** | JWT (HS256), BCrypt, Spring Security 6 Filter Chain, MapStruct, Flyway, JPA Auditing, Bean Validation |
| **product-service** | Elasticsearch (inverted index), Dual-write strategy, Spring Data ES, Pagination (Page vs Slice) |
| **order-service** | Saga Pattern (Orchestration), Kafka Producer, Kafka Consumer, Idempotency, Eventual Consistency, Resilience4j Retry |
| **inventory-service** | Redis Cache (@Cacheable, @CacheEvict), Cache-Aside Pattern, Optimistic Locking (@Version), Race Conditions |
| **notification-service** | Event-Driven Design, Fire-and-Forget, Dead Letter Topics, Thymeleaf Email Templates |
| **common-lib** | Java Library Plugin, DRY Principle, Generic API Responses, Event Base Classes |

---

## Prerequisites

- **Java 21** (with preview features)
- **Docker 24+** and **Docker Compose v2**
- **Gradle 8.6** (or use the included wrapper: `./gradlew`)

Check versions:
```bash
java --version   # Should show: openjdk 21
docker --version # Should show: Docker version 24+
```

---

## Quick Start

### 1. Start Infrastructure

```bash
cd smartshop/
docker-compose up -d
```

Wait for all services to be healthy (~60 seconds for Elasticsearch):
```bash
docker-compose ps
# All should show: healthy
```

### 2. Start Services (in order)

Services must start in this order due to dependencies:

```bash
# Terminal 1: Service Registry (must start first)
./gradlew :service-registry:bootRun

# Terminal 2: Config Server (needs service-registry)
./gradlew :config-server:bootRun

# Terminal 3-8: Business Services (any order after config-server)
./gradlew :user-service:bootRun
./gradlew :product-service:bootRun
./gradlew :order-service:bootRun
./gradlew :inventory-service:bootRun
./gradlew :notification-service:bootRun

# Terminal 9: API Gateway (start last, after all services are up)
./gradlew :api-gateway:bootRun
```

### 3. Verify Everything is Running

| URL | Description |
|-----|-------------|
| http://localhost:8761 | Eureka Dashboard (all services should appear) |
| http://localhost:8888/user-service/default | Config Server (user-service config) |
| http://localhost:8080/actuator/gateway/routes | API Gateway routes |
| http://localhost:9411 | Zipkin Distributed Tracing UI |
| http://localhost:8090 | Kafka UI (topics, messages, consumer groups) |
| http://localhost:8081/swagger-ui.html | User Service API Docs |
| http://localhost:8082/swagger-ui.html | Product Service API Docs |
| http://localhost:8083/swagger-ui.html | Order Service API Docs |
| http://localhost:8084/swagger-ui.html | Inventory Service API Docs |

---

## API Examples (via API Gateway on port 8080)

### Authentication

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "Password123",
    "firstName": "John",
    "lastName": "Doe"
  }'

# Response: { "success": true, "data": { "accessToken": "eyJhbG...", "userId": 2, ... } }

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"emailOrUsername": "john@example.com", "password": "Password123"}'

# Set the token for subsequent requests
TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

### Products

```bash
# List products (no auth required)
curl http://localhost:8080/api/products

# Search products
curl "http://localhost:8080/api/products/search?query=iphone&minPrice=500&maxPrice=1500"

# Get product by ID
curl http://localhost:8080/api/products/1

# Create product (requires auth)
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Samsung Galaxy S24",
    "description": "Latest Samsung flagship smartphone",
    "sku": "SAMSUNG-GS24-001",
    "price": 799.99,
    "categoryId": 6,
    "brand": "Samsung"
  }'
```

### Orders

```bash
# Place an order
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "shippingAddress": "123 Main Street, New York, NY 10001",
    "items": [
      {
        "productId": 1,
        "productName": "iPhone 15 Pro",
        "sku": "APPLE-IP15P-001",
        "quantity": 1,
        "unitPrice": 999.99
      }
    ]
  }'

# Get order status
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/orders/1

# Get all orders for current user
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/orders/user/2
```

### Inventory

```bash
# Check stock for a product
curl http://localhost:8080/api/inventory/1

# Response: { "productId": 1, "quantity": 100, "reservedQuantity": 0, "availableQuantity": 100 }
```

---

## Kafka Topics

| Topic | Producer | Consumers | Purpose |
|-------|----------|-----------|---------|
| `order.created` | order-service | inventory-service, notification-service | New order placed — trigger stock reservation and notification |
| `order.confirmed` | order-service | notification-service | Order confirmed — send confirmation email |
| `order.cancelled` | order-service | inventory-service, notification-service | Order cancelled — release stock, send cancellation email |
| `inventory.checked` | inventory-service | order-service | Stock check result — advance or compensate the Saga |
| `*.DLT` | Spring Kafka | Ops team | Dead Letter Topics — messages that failed after max retries |

---

## Service Ports

| Service | Port | Database |
|---------|------|----------|
| api-gateway | 8080 | — |
| user-service | 8081 | PostgreSQL: smartshop_users |
| product-service | 8082 | PostgreSQL: smartshop_products + Elasticsearch |
| order-service | 8083 | PostgreSQL: smartshop_orders |
| inventory-service | 8084 | PostgreSQL: smartshop_inventory + Redis |
| notification-service | 8085 | — (no DB) |
| service-registry | 8761 | — |
| config-server | 8888 | — |
| PostgreSQL | 5432 | — |
| Redis | 6379 | — |
| Elasticsearch | 9200 | — |
| Kafka | 9092/9093 | — |
| ZooKeeper | 2181 | — |
| Zipkin | 9411 | — |
| Kafka UI | 8090 | — |

---

## Architectural Patterns Glossary

| Pattern | Used In | Description |
|---------|---------|-------------|
| **Microservices** | Entire platform | Independently deployable services, each owning its domain and database |
| **API Gateway** | api-gateway | Single entry point for all external traffic; handles cross-cutting concerns |
| **Service Discovery** | service-registry | Dynamic service registration and lookup (no hardcoded URLs) |
| **Externalized Configuration** | config-server | 12-Factor Principle III: config stored outside application code |
| **Saga (Orchestration)** | order-service | Distributed transaction coordination via events with compensating transactions |
| **Event-Driven Architecture** | All services | Services communicate via Kafka events rather than synchronous HTTP |
| **Cache-Aside** | inventory-service | Read from cache; on miss, read from DB and populate cache |
| **Circuit Breaker** | api-gateway, order-service | Fail fast when a service is down; prevent cascading failures |
| **JWT Authentication** | user-service, api-gateway | Stateless authentication; tokens carry all user identity information |
| **Distributed Tracing** | All services | End-to-end request tracing across service boundaries via Zipkin |
| **Database per Service** | All services | Each service has its own isolated database; no shared DB access |
| **Dual Write** | product-service | Write to both PostgreSQL and Elasticsearch for consistency and search performance |
| **Optimistic Locking** | inventory-service | Conflict detection without holding DB locks; enables high throughput |
| **Dead Letter Topic** | Kafka consumers | Failed messages parked for ops investigation and replay |

---

## Build Commands

```bash
# Build all modules
./gradlew build

# Build a specific service
./gradlew :user-service:build

# Run tests for a service
./gradlew :user-service:test

# Run all tests
./gradlew test

# Create executable JARs for all services
./gradlew bootJar

# Check dependency versions
./gradlew dependencyInsight --dependency spring-kafka --configuration runtimeClasspath
```

---

## Project Structure

```
smartshop/
├── build.gradle.kts          ← Root build: shared config for all submodules
├── settings.gradle.kts       ← Declares all submodules
├── gradle.properties         ← All dependency versions centralized here
├── docker-compose.yml        ← All infrastructure (PostgreSQL, Redis, Kafka, ES, Zipkin)
├── init-scripts/
│   └── postgres-init.sql     ← Creates per-service databases on first start
│
├── common-lib/               ← Shared library (not a Spring Boot app)
│   └── dto/                  ← ApiResponse<T>, ErrorResponse
│   └── exception/            ← BaseException hierarchy
│   └── event/                ← BaseEvent for all Kafka events
│   └── util/                 ← DateUtils, JsonUtils
│
├── service-registry/         ← Eureka Server (:8761)
├── config-server/            ← Spring Cloud Config (:8888)
│   └── config-repo/          ← Config files for each service
│
├── api-gateway/              ← Spring Cloud Gateway (:8080)
│   └── filter/               ← AuthenticationFilter, LoggingFilter
│   └── config/               ← RouteConfig, RateLimiterConfig, CircuitBreakerConfig
│
├── user-service/             ← Auth + Users (:8081)
├── product-service/          ← Products + Search (:8082)
├── order-service/            ← Orders + Saga (:8083)
├── inventory-service/        ← Stock + Cache (:8084)
└── notification-service/     ← Email Notifications (:8085)
```
