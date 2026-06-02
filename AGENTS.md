# AGENTS.md

## Cursor Cloud specific instructions

### Monorepo layout

| Path | Stack | Dev port |
|------|-------|----------|
| `smartshop/` | Spring Boot 3.2 microservices (Gradle) | API Gateway **8080**, Eureka **8761** |
| `smartshop-frontend/` | React 18 + Vite 5 | **5173** |

Standard commands are in each folder's `README.md`. This section covers non-obvious Cloud VM setup only.

### Infrastructure (Docker)

From `smartshop/`:

```bash
docker compose up -d
```

`docker-compose.override.yml` is merged automatically and points Zookeeper/Kafka at **`bitnamilegacy/*`** images (Bitnami removed free tags from Docker Hub). If compose fails on `bitnami/zookeeper` or `bitnami/kafka`, ensure that override file exists.

Containers: Postgres **5432**, Redis **6379**, Elasticsearch **9200**, Kafka **29092**, Zipkin **9411**.

Use `sudo docker …` if the agent user is not in the `docker` group yet.

### Backend startup order

1. `docker compose up -d` (in `smartshop/`)
2. Eureka: `./gradlew :service-registry:bootRun` (wait for `http://localhost:8761/actuator/health`)
3. Core services (separate terminals or tmux): `:api-gateway`, `:user-service`, `:product-service`, `:order-service`, `:inventory-service` via `./gradlew :<service>:bootRun`
4. Optional: `:config-server`, `:notification-service`

Set `JAVA_HOME` to a Java 21 JDK (e.g. `/usr/lib/jvm/java-21-openjdk-amd64`) if Gradle warns about missing Java.

**API Gateway auth:** Most routes require JWT. Public paths include `/auth/register`, `/auth/login`, and actuator/swagger. The storefront logs in before calling `/products`.

**Product-service:** JPA repos live under `repository.jpa`, Elasticsearch repos under `repository.search`, configured in `DataRepositoryConfig`. Do not add duplicate `*Repository` interfaces in the parent `repository` package.

### Frontend

```bash
cd smartshop-frontend && npm run dev -- --host 0.0.0.0
```

Vite proxies `/api` → `http://localhost:8080`. Gateway must be up for catalog/checkout flows.

### Lint / test (no full stack required)

| Area | Command |
|------|---------|
| Frontend lint | `cd smartshop-frontend && npm run lint` |
| Frontend unit tests | `cd smartshop-frontend && npm test` |
| Backend compile | `cd smartshop && ./gradlew build -x test` |

Backend modules have **no** `src/test` suites in this repo; `build -x test` is the usual backend check.

### tmux

Long-running `bootRun` / `npm run dev` processes should use tmux (`tmux -f /exec-daemon/tmux.portal.conf`) so sessions survive after the setup agent exits.
