/*
 * =============================================================================
 * service-registry/build.gradle.kts — Eureka Server module
 * =============================================================================
 *
 * This module is a runnable Spring Boot application, so it applies the
 * `org.springframework.boot` plugin (which produces the executable fat JAR and
 * provides `bootRun`).
 *
 * The only meaningful dependency is the Eureka *server* starter. Note: the
 * server does NOT use the Eureka *client* starter — it is the registry itself,
 * not a participant that registers into another registry.
 * =============================================================================
 */

plugins {
    id("org.springframework.boot")
}

dependencies {
    // Brings in Netflix Eureka Server + its embedded dashboard UI.
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    // Actuator exposes /actuator/health so orchestrators can probe the registry.
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
