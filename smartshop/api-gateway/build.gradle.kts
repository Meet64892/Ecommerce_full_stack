// =============================================================================
// api-gateway/build.gradle.kts — Spring Cloud Gateway Build Config
// =============================================================================
//
// IMPORTANT: Spring Cloud Gateway is built on Spring WebFlux (reactive/non-blocking).
// It is INCOMPATIBLE with spring-boot-starter-web (which is blocking/servlet-based).
// If you add spring-boot-starter-web here, the application WILL fail to start with:
// "Spring MVC found on classpath, which is incompatible with Spring Cloud Gateway"
//
// WHY REACTIVE (WebFlux)?
// Traditional blocking web servers (Tomcat with Servlets) create one thread per request.
// An API gateway handles ALL traffic — if it blocks waiting for upstream service responses,
// it needs hundreds of threads. WebFlux uses an event loop (like Node.js) — a small number
// of threads handle thousands of concurrent connections by never blocking.
// =============================================================================

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
}

val springCloudVersion: String by project
val lombokVersion: String by rootProject.extra
val springdocVersion: String by rootProject.extra

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

dependencies {
    // Spring Cloud Gateway: reactive routing, filter chain, predicates
    // Internally uses Reactor Netty (not Tomcat) as the HTTP server
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")

    // Eureka Client: gateway discovers upstream services by name
    // (e.g., lb://user-service resolves via Eureka to actual IP:port)
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // Spring Cloud Config Client: fetches routing and filter config from config-server
    implementation("org.springframework.cloud:spring-cloud-starter-config")

    // Redis for rate limiting (RequestRateLimiter filter uses Redis to count requests)
    // Redis is used here because rate limit counters must be shared across multiple
    // gateway instances. An in-memory counter would only work with one instance.
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    // Resilience4j for circuit breaking on routes
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")

    // Spring Security (reactive) for JWT validation in the filter
    implementation("org.springframework.boot:spring-boot-starter-security")

    // JWT library for token parsing in the authentication filter
    implementation("io.jsonwebtoken:jjwt-api:${rootProject.extra["jjwtVersion"]}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${rootProject.extra["jjwtVersion"]}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${rootProject.extra["jjwtVersion"]}")

    // Actuator for health and metrics
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Micrometer Tracing with Zipkin for distributed traces
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // SpringDoc for reactive WebFlux (NOT the regular webmvc version)
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:${springdocVersion}")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
}
