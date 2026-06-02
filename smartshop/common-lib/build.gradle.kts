/*
 * =============================================================================
 * common-lib/build.gradle.kts — Shared library module
 * =============================================================================
 *
 * WHY `java-library` AND NOT `org.springframework.boot`?
 * ------------------------------------------------------
 * The Spring Boot plugin's main job is to produce an *executable fat JAR* (a
 * runnable application with an embedded server and a `main` method). common-lib
 * is NOT an application — it is a plain library of shared classes (DTOs,
 * exceptions, base events, utilities) that other services put on their
 * classpath. Applying the Spring Boot plugin here would try to build a bootable
 * archive with no main class and would repackage the JAR in a way that other
 * modules cannot consume as a normal dependency.
 *
 * The `java-library` plugin adds the crucial `api` vs `implementation`
 * distinction:
 *   * api            — dependency leaks onto the consumer's compile classpath
 *                      (use when types appear in our public method signatures).
 *   * implementation — dependency stays internal (faster recompilation).
 *
 * DRY PRINCIPLE
 * -------------
 * Every microservice needs a consistent API envelope, standardized errors, and
 * a common Kafka event base. Defining them once here means a fix or a new field
 * propagates everywhere automatically — there is exactly one ApiResponse shape
 * across the entire platform.
 * =============================================================================
 */

plugins {
    // Library semantics (api/implementation), no bootJar.
    `java-library`
}

dependencies {
    // Jackson for JSON (de)serialization helpers in JsonUtils and for
    // annotating DTOs. `api` because consumers serialize these types directly.
    api("com.fasterxml.jackson.core:jackson-databind:2.15.4")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.4")

    // Bean Validation annotations used on shared DTOs.
    api("jakarta.validation:jakarta.validation-api:3.0.2")
}
