/*
 * =============================================================================
 * product-service/build.gradle.kts — Catalog service (PostgreSQL + Elasticsearch)
 * =============================================================================
 *
 * Demonstrates DUAL persistence: the relational source of truth lives in
 * PostgreSQL (JPA), while a denormalized copy lives in Elasticsearch for fast
 * full-text search. Hence both spring-boot-starter-data-jpa AND
 * spring-boot-starter-data-elasticsearch are present.
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
    // Elasticsearch repositories + @Document mapping for the search index.
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
}
