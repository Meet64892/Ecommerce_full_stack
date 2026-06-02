/*
 * =============================================================================
 * api-gateway/build.gradle.kts — Spring Cloud Gateway module
 * =============================================================================
 *
 * Spring Cloud Gateway is built on the *reactive* stack (Spring WebFlux +
 * Project Reactor + Netty), NOT the servlet stack. That is why we pull the
 * gateway starter (which transitively brings WebFlux) and the *reactive* Redis
 * rate-limiter support, and we do NOT add spring-boot-starter-web (mixing the
 * blocking servlet stack with the reactive gateway breaks the auto-config).
 * =============================================================================
 */

plugins {
    id("org.springframework.boot")
}

// Pulled from gradle.properties for the JWT parsing library used by the
// edge AuthenticationFilter (must match user-service's signing library).
val jjwtVersion: String by project

dependencies {
    // Shared DTOs (ApiResponse) for uniform fallback bodies.
    implementation(project(":common-lib"))
    // The reactive API gateway (routing, predicates, filters) on Netty.
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    // Eureka client so the gateway can route by service id (lb://user-service).
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    // Pulls externalized config from the config-server at startup.
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    // Resilience4j integration for circuit-breaker filters + fallbacks (reactive).
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
    // Reactive Redis: backs the RequestRateLimiter token-bucket counters.
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    // Actuator: /actuator/gateway routes + health.
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // Micrometer Tracing -> Zipkin (Brave/B3) for distributed tracing.
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // jjwt: parse + verify the HS256 JWT at the gateway boundary.
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}
