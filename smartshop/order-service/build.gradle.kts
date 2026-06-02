/*
 * =============================================================================
 * order-service/build.gradle.kts — Orders + Saga orchestration + Kafka
 * =============================================================================
 *
 * This service both PRODUCES events (order.created) and CONSUMES replies
 * (inventory.checked), so it needs spring-kafka. It also uses Resilience4j for
 * retry + circuit breaking around the cross-service interactions.
 * =============================================================================
 */

plugins {
    id("org.springframework.boot")
}

val springdocVersion: String by project
val resilience4jVersion: String by project

dependencies {
    implementation(project(":common-lib"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")

    // Kafka producer + consumer support.
    implementation("org.springframework.kafka:spring-kafka")

    // Resilience4j Spring Boot 3 integration (retry, circuit breaker annotations).
    implementation("io.github.resilience4j:resilience4j-spring-boot3:$resilience4jVersion")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testRuntimeOnly("com.h2database:h2")
}
