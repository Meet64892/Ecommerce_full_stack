/*
 * =============================================================================
 * user-service/build.gradle.kts — Authentication + user management service
 * =============================================================================
 *
 * This is a classic servlet-stack Spring Boot service (web + JPA + security).
 * It registers in Eureka, pulls config from the config-server, exposes Swagger,
 * persists to PostgreSQL with Flyway-managed schema, and signs JWTs.
 * =============================================================================
 */

plugins {
    id("org.springframework.boot")
}

// Centralized versions from gradle.properties.
val jjwtVersion: String by project
val springdocVersion: String by project

dependencies {
    // Shared API envelope, base exceptions, base events.
    implementation(project(":common-lib"))

    // --- Web / persistence / validation ------------------------------------
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // --- Cloud: discovery + externalized config ----------------------------
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")

    // --- Observability ------------------------------------------------------
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // --- API docs -----------------------------------------------------------
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    // --- JWT (jjwt split into api/impl/jackson) -----------------------------
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    // --- Database driver + migrations ---------------------------------------
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    // H2 in-memory DB lets tests run without a real PostgreSQL.
    testRuntimeOnly("com.h2database:h2")
}
