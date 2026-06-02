/*
 * =============================================================================
 * inventory-service/build.gradle.kts — Stock management + Redis cache + Kafka
 * =============================================================================
 *
 * Consumes order.created (reserve stock) and publishes inventory.checked back to
 * the order saga. Uses Redis as a read cache in front of PostgreSQL.
 * =============================================================================
 */

plugins {
    id("org.springframework.boot")
}

val springdocVersion: String by project

dependencies {
    implementation(project(":common-lib"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Redis: RedisTemplate + Spring Cache abstraction backed by Redis.
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")

    implementation("org.springframework.kafka:spring-kafka")

    // spring-retry + AOP power the @Retryable optimistic-lock retry in the service.
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
}
