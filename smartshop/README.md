# SmartShop - Enterprise E-Commerce Microservices Platform

SmartShop is an educational, production-shaped Spring Boot 3.2 microservices platform. The code is intentionally comment-heavy: classes, configuration files, and infrastructure definitions explain why each piece exists so the repository can be read like a guided textbook.

## Architecture

```text
                            +-------------------+
                            |  Client / Browser |
                            +---------+---------+
                                      |
                                      v
+-------------------+        +-------------------+        +-------------------+
| Service Registry  |<------>|    API Gateway    |------->|  Zipkin Tracing   |
| Eureka :8761      |        | :8080             |        | :9411             |
+---------+---------+        +---+---+---+---+---+        +-------------------+
          ^                      |   |   |   |
          |                      |   |   |   +------------------+
          |                      |   |   +----------------+     |
          |                      |   +------------+       |     |
          |                      v                v       v     v
+---------+---------+  +---------+--------+ +------+-----+ +----+-------------+
| Config Server     |  | User Service     | |Product Svc | | Order Service    |
| :8888             |  | :8081 + Postgres | |:8082 + ES  | | :8083 + Kafka    |
+-------------------+  +------------------+ +------------+ +----+-------------+
                                                                  |
                                                                  v
                                                        +---------+-----------+
                                                        | Inventory Service   |
                                                        | :8084 + Redis + DB  |
                                                        +---------+-----------+
                                                                  |
                                                                  v
                                                        +---------+-----------+
                                                        | Notification Svc    |
                                                        | :8085 + Kafka       |
                                                        +---------------------+
```

## What you'll learn

| Module | Concepts |
| --- | --- |
| `service-registry` | Eureka service discovery, heartbeats, lease renewal, client lookup |
| `config-server` | Centralized configuration, 12-factor configuration, native config repositories |
| `api-gateway` | Gateway routing, JWT validation, correlation IDs, Redis rate limiting, circuit breakers |
| `user-service` | Spring Security 6, JWT, BCrypt, DTOs, MapStruct, Flyway, auditing |
| `product-service` | JPA plus Elasticsearch dual writes, full-text search, pagination |
| `order-service` | Saga orchestration, Kafka events, eventual consistency, resilience patterns |
| `inventory-service` | Redis cache-aside, optimistic locking, race-condition-safe stock reservation |
| `notification-service` | Async notifications, consumer groups, dead-letter topic concepts |
| `common-lib` | Shared DTOs, shared exceptions, base event metadata, reusable utilities |

## Prerequisites

- Java 21
- Docker and Docker Compose
- Gradle 8.6 (or a generated Gradle wrapper using Gradle 8.6)

## Setup

```bash
cd smartshop
docker-compose up -d
./gradlew :service-registry:bootRun
./gradlew :config-server:bootRun
./gradlew :api-gateway:bootRun
./gradlew :user-service:bootRun
./gradlew :product-service:bootRun
./gradlew :order-service:bootRun
./gradlew :inventory-service:bootRun
./gradlew :notification-service:bootRun
```

The services use `optional:configserver:` imports so they can still boot locally if the config server is temporarily unavailable.

## API examples

```bash
# Register and authenticate a user
curl -X POST http://localhost:8080/auth/register -H 'Content-Type: application/json' \
  -d '{"email":"ada@example.com","password":"Secret123!","firstName":"Ada","lastName":"Lovelace"}'
curl -X POST http://localhost:8080/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"ada@example.com","password":"Secret123!"}'

# Products
curl http://localhost:8080/products
curl -X POST http://localhost:8080/products -H 'Content-Type: application/json' \
  -d '{"name":"Mechanical Keyboard","description":"Tactile keyboard","price":129.99,"categoryId":"00000000-0000-0000-0000-000000000001","stockKeepingUnit":"KB-001"}'

# Orders
curl -X POST http://localhost:8080/orders -H 'Content-Type: application/json' \
  -d '{"userId":"00000000-0000-0000-0000-000000000001","items":[{"productId":"00000000-0000-0000-0000-000000000002","quantity":1,"unitPrice":129.99}]}'

# Inventory
curl http://localhost:8080/inventory/00000000-0000-0000-0000-000000000002
curl -X PUT http://localhost:8080/inventory/00000000-0000-0000-0000-000000000002/reserve -H 'Content-Type: application/json' -d '{"quantity":1}'
```

## Kafka topics

| Topic | Producer | Consumer | Purpose |
| --- | --- | --- | --- |
| `order.created` | order-service | inventory-service | Requests stock reservation after checkout |
| `inventory.checked` | inventory-service | order-service | Reports reservation success or failure |
| `order.confirmed` | order-service | notification-service | Sends confirmation email/SMS |
| `order.cancelled` | order-service | notification-service | Sends cancellation notice |
| `user.registered` | user-service | notification-service | Sends welcome email |

## Service ports

| Service | Port |
| --- | ---: |
| API Gateway | 8080 |
| User Service | 8081 |
| Product Service | 8082 |
| Order Service | 8083 |
| Inventory Service | 8084 |
| Notification Service | 8085 |
| Service Registry | 8761 |
| Config Server | 8888 |
| Zipkin | 9411 |

## Glossary

- **API Gateway**: A single entry point that centralizes routing and cross-cutting behavior such as authentication and rate limiting.
- **Service Discovery**: Runtime lookup that lets services call logical names instead of hard-coded hosts.
- **Saga Pattern**: A sequence of local transactions coordinated with compensating actions instead of one distributed transaction.
- **Circuit Breaker**: A state machine that stops calls to a failing dependency so the system can recover.
- **Cache-Aside**: The application checks the cache first, loads from the database on miss, then stores the result with a TTL.
- **Optimistic Locking**: Version-based concurrency control that detects conflicting writes at commit time.
- **Eventual Consistency**: Data converges after asynchronous events complete rather than being instantly consistent everywhere.
- **OpenAPI**: A machine-readable API contract that powers generated docs and clients.
- **Distributed Tracing**: Propagating trace identifiers through service calls so one request can be followed end to end.
