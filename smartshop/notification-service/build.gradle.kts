/*
 * =============================================================================
 * notification-service/build.gradle.kts — Kafka-driven notifications
 * =============================================================================
 *
 * Pure event consumer: no database. Listens to order/user events and renders
 * emails from Thymeleaf templates. Includes spring-boot-starter-web only so it
 * exposes actuator/swagger; the core work is Kafka consumption.
 * =============================================================================
 */

plugins {
    id("org.springframework.boot")
}

val springdocVersion: String by project

dependencies {
    implementation(project(":common-lib"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Mail sender abstraction (JavaMailSender). We use it in a mock-friendly way.
    implementation("org.springframework.boot:spring-boot-starter-mail")
    // Thymeleaf renders the HTML email bodies from templates.
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")

    implementation("org.springframework.kafka:spring-kafka")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
