// =============================================================================
// settings.gradle.kts — Multi-Module Project Settings
// =============================================================================
//
// PURPOSE OF THIS FILE:
// settings.gradle.kts is the FIRST file Gradle reads when starting a build.
// It serves two critical purposes:
//   1. Declares the ROOT PROJECT NAME — this becomes the artifact group prefix
//      and is how Gradle identifies this entire project.
//   2. Registers all SUBMODULES (called "subprojects" or "included builds") —
//      Gradle needs to know which folders are modules before it reads any
//      build.gradle.kts files within them.
//
// WHAT IS A MULTI-MODULE PROJECT?
// Instead of having one giant monolithic application, we split the codebase into
// multiple modules. Each module has its own build.gradle.kts, source code, and
// produces its own JAR artifact. This enables:
//   - Independent compilation (change user-service → only user-service recompiles)
//   - Shared libraries (common-lib can be depended on by any service)
//   - Clear ownership boundaries between teams
//
// HOW SUBMODULE RESOLUTION WORKS:
// When you write include(":user-service"), Gradle looks for a folder named
// "user-service" relative to this file's location and expects a build.gradle.kts
// inside it. The colon prefix ":" denotes the root project; ":user-service" means
// a direct child of the root.
// =============================================================================

// The root project name — used as the artifact group in Maven coordinates
rootProject.name = "smartshop"

// =============================================================================
// INFRASTRUCTURE SERVICES
// These support the microservices but don't serve business features directly
// =============================================================================

// Eureka-based service registry — all microservices register here so they can
// discover each other by name rather than hardcoded host:port
include(":service-registry")

// Spring Cloud Config Server — serves externalized configuration to all services
// This implements the 12-factor app principle: "Store config in the environment"
include(":config-server")

// API Gateway — the single entry point for all external traffic; handles routing,
// authentication verification, rate limiting, and circuit breaking
include(":api-gateway")

// =============================================================================
// BUSINESS DOMAIN SERVICES
// Each service owns its own domain, database, and business logic
// =============================================================================

// Handles user registration, authentication, and JWT token issuance
include(":user-service")

// Manages product catalog with full-text search via Elasticsearch
include(":product-service")

// Manages order lifecycle; orchestrates the Saga pattern across services
include(":order-service")

// Manages stock levels with Redis caching and optimistic locking
include(":inventory-service")

// Consumes Kafka events and sends email/SMS notifications asynchronously
include(":notification-service")

// =============================================================================
// SHARED LIBRARY
// Not a Spring Boot app — a plain Java library that all services depend on
// Contains: common DTOs, base exceptions, utility classes, Kafka event POJOs
// =============================================================================
include(":common-lib")
