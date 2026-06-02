# SmartShop - Enterprise E-Commerce Microservices Platform

SmartShop is a production-style educational project that demonstrates a Spring Boot 3 microservice architecture with API gateway, service discovery, centralized configuration, event-driven communication, distributed tracing, and resilience patterns.

## Architecture Diagram

```text
                       +---------------------------+
                       |        API Clients        |
                       +-------------+-------------+
                                     |
                                     v
                          +----------+----------+
                          |      API Gateway    |
                          |       :8080         |
                          +----+----+----+------+
                               |    |    |
       +-----------------------+    |    +----------------------+
       v                            v                           v
+------+-------+            +-------+--------+           +------+--------+
| user-service |            | product-service|           | order-service |
|    :8081     |            |     :8082      |           |    :8083      |
+------+-------+            +-------+--------+           +------+--------+
       |                            |                           |
       v                            v                           v
+------+-------+            +-------+--------+           +------+--------+
| PostgreSQL   |            | PostgreSQL + ES|           | PostgreSQL    |
+--------------+            +----------------+           +------+--------+
                                                                |
                                                                v
                                                      +---------+---------+
                                                      | inventory-service |
                                                      |      :8084        |
                                                      +----+---------+----+
                                                           |         |
                                                           v         v
                                                       PostgreSQL   Redis

                         +----------------------------------------------+
                         | notification-service :8085 (Kafka consumers) |
                         +----------------------------------------------+

Infra: Eureka(:8761), Config Server(:8888), Kafka/Zookeeper, Zipkin(:9411)
```

## What You Will Learn

- **service-registry**: service discovery, leases, heartbeats, and dynamic endpoint lookup.
- **config-server**: externalized configuration and 12-factor app config principles.
- **api-gateway**: centralized auth, routing, rate limiting, logging, and circuit breakers.
- **user-service**: JWT auth, validation, Flyway migrations, and security filter chain.
- **product-service**: dual persistence (PostgreSQL + Elasticsearch), pagination, and search.
- **order-service**: saga orchestration, Kafka events, eventual consistency, resiliency.
- **inventory-service**: caching, optimistic locking, and stock reservation race-condition controls.
- **notification-service**: asynchronous event consumers and email templating.
- **common-lib**: shared contracts, errors, utilities, and event base models.

## Prerequisites

- Java 21
- Docker + Docker Compose
- Gradle 8.6+ (wrapper recommended)

## Setup

1. Start infrastructure:
   ```bash
   docker-compose up -d
   ```
2. Start core platform services first:
   ```bash
   ./gradlew :service-registry:bootRun :config-server:bootRun
   ```
3. Start business services:
   ```bash
   ./gradlew :user-service:bootRun :product-service:bootRun :inventory-service:bootRun :order-service:bootRun :notification-service:bootRun
   ```
4. Start gateway:
   ```bash
   ./gradlew :api-gateway:bootRun
   ```

## API Examples

```bash
# Register user
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{"email":"alice@example.com","password":"Password123!","firstName":"Alice","lastName":"Doe"}'

# Login
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"email":"alice@example.com","password":"Password123!"}'

# Create product
curl -X POST http://localhost:8080/products -H "Authorization: Bearer <token>" -H "Content-Type: application/json" -d '{"name":"Laptop","description":"Fast laptop","price":1299.0,"categoryId":1,"rating":5}'

# Create order
curl -X POST http://localhost:8080/orders -H "Authorization: Bearer <token>" -H "Content-Type: application/json" -d '{"userId":1,"items":[{"productId":1,"quantity":1,"unitPrice":1299.0}]}'
```

## Kafka Topics

| Topic | Producer | Consumer | Purpose |
|---|---|---|---|
| order.created | order-service | inventory-service | Trigger stock reservation flow |
| inventory.checked | inventory-service | order-service | Continue/abort saga based on stock |
| order.confirmed | order-service | notification-service | Notify user of successful order |
| order.cancelled | order-service | notification-service | Notify user of cancellation |
| user.registered | user-service | notification-service | Send welcome email |

## Service Ports

| Service | Port |
|---|---|
| API Gateway | 8080 |
| User Service | 8081 |
| Product Service | 8082 |
| Order Service | 8083 |
| Inventory Service | 8084 |
| Notification Service | 8085 |
| Eureka Registry | 8761 |
| Config Server | 8888 |
| Zipkin | 9411 |

## Glossary

- **API Gateway**: Single ingress for routing/auth/rate-limiting.
- **Service Discovery**: Dynamic registration and lookup of service instances.
- **Saga Pattern**: Sequence of local transactions with compensating actions.
- **Eventual Consistency**: Data converges asynchronously across services.
- **Circuit Breaker**: Fails fast to protect systems during downstream failures.
- **Cache-Aside**: App loads cache on miss and updates source of truth first.
- **Optimistic Locking**: Version-based conflict detection for concurrent writes.
- **Distributed Tracing**: End-to-end request flow visibility using trace/span IDs.
