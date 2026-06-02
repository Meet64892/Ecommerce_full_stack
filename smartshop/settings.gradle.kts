/*
 * =============================================================================
 * settings.gradle.kts — The "table of contents" of a multi-module Gradle build
 * =============================================================================
 *
 * WHAT settings.gradle.kts DOES
 * -----------------------------
 * Gradle evaluates this file FIRST, before any build.gradle.kts. Its job is to
 * define the *structure* of the build:
 *   1. rootProject.name — the logical name of the whole build.
 *   2. include(...)      — every sub-project (here: every microservice) that
 *                          participates in this build. Each included name maps
 *                          to a folder containing its own build.gradle.kts.
 *
 * Without an `include("foo")` line, Gradle has no idea the `foo/` directory is
 * part of the build, even if a build.gradle.kts sits inside it. This file is
 * therefore the single registry that wires the modules together so they can
 * depend on each other (e.g. every service depends on :common-lib).
 *
 * pluginManagement {} below tells Gradle WHERE to download plugins from and
 * lets us centralize plugin versions so individual modules can apply plugins
 * by id without repeating the version.
 * =============================================================================
 */

pluginManagement {
    // Repositories Gradle searches when resolving plugins declared in modules.
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

// The name shown in build scans and used as the root artifact group context.
rootProject.name = "smartshop"

// --- Infrastructure / platform services -------------------------------------
include("common-lib")        // Shared DTOs, exceptions, events — used by all services.
include("service-registry")  // Eureka server: where services find each other.
include("config-server")     // Centralized externalized configuration.
include("api-gateway")        // Single ingress point for all client traffic.

// --- Business microservices --------------------------------------------------
include("user-service")          // Authentication, JWT, user profiles.
include("product-service")       // Product catalog + Elasticsearch search.
include("order-service")         // Order lifecycle + Saga orchestration.
include("inventory-service")     // Stock management + Redis caching.
include("notification-service")  // Email/SMS reactions to Kafka events.
