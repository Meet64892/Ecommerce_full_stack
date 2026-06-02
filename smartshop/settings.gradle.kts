/*
 * settings.gradle.kts - SmartShop multi-module settings
 *
 * In a multi-module Gradle build, this file is the entry-point that tells Gradle
 * which subprojects participate in the build graph. Without this file, Gradle would
 * only know about the root project and would not resolve inter-module dependencies.
 */
rootProject.name = "smartshop"

include(
    "service-registry",
    "config-server",
    "api-gateway",
    "user-service",
    "product-service",
    "order-service",
    "inventory-service",
    "notification-service",
    "common-lib"
)
