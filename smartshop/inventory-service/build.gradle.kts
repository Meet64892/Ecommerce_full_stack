// =============================================================================
// inventory-service/build.gradle.kts — Inventory Service Build Config
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
    implementation(project(":common-lib"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.flywaydb:flyway-core:10.8.1")
    implementation("org.flywaydb:flyway-database-postgresql:10.8.1")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Redis for caching inventory data
    // Spring Cache abstraction + Redis: @Cacheable reads from Redis,
    // @CacheEvict removes stale entries on write
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    // Spring Retry: enables @Retryable for optimistic locking conflict resolution
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework:spring-aspects")

    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}
