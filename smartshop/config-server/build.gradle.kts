/*
 * =============================================================================
 * config-server/build.gradle.kts — Spring Cloud Config Server module
 * =============================================================================
 *
 * Runnable Spring Boot app that serves externalized configuration to every
 * other service. It does NOT need the Eureka client here (it is bootstrap
 * infrastructure that clients reach by URL), keeping its startup independent.
 * =============================================================================
 */

plugins {
    id("org.springframework.boot")
}

dependencies {
    // The Config Server: serves config from a backend (git, native filesystem...).
    implementation("org.springframework.cloud:spring-cloud-config-server")
    // Health endpoint so we can verify the config plane is up.
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
