/*
 * settings.gradle.kts is the entry point Gradle reads before any build.gradle.kts file.
 * In a multi-module build it gives the whole repository a single logical name and registers
 * each subproject so Gradle can build, test, and publish modules together while still letting
 * every service keep its own dependencies and Spring Boot packaging rules.
 */
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "smartshop"

include(
    "common-lib",
    "service-registry",
    "config-server",
    "api-gateway",
    "user-service",
    "product-service",
    "order-service",
    "inventory-service",
    "notification-service"
)
