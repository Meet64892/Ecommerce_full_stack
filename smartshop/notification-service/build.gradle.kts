// =============================================================================
// notification-service/build.gradle.kts
// =============================================================================
//
// WHY A SEPARATE NOTIFICATION SERVICE?
// Single Responsibility Principle: notification delivery (email, SMS, push) is
// a separate concern from order management or user management.
// Async via Kafka: notifications don't need to be synchronous.
// If the email server is down, we don't want order placement to fail.
// The Kafka message stays in the topic until the notification-service processes it.

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
}

val springCloudVersion: String by project
val lombokVersion: String by rootProject.extra

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

dependencies {
    implementation(project(":common-lib"))

    // Web: minimal — notification service has no external API endpoints
    // (it only consumes Kafka and sends emails)
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Thymeleaf: server-side template engine for HTML email generation
    // Templates live in src/main/resources/templates/
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // Spring Mail: wraps JavaMail API for sending emails
    implementation("org.springframework.boot:spring-boot-starter-mail")

    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}
