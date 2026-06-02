// =============================================================================
// service-registry/build.gradle.kts — Eureka Server Build Config
// =============================================================================
//
// The service registry requires only the Spring Cloud Netflix Eureka Server
// dependency. No database, no security (in dev mode), no Kafka — just discovery.
// =============================================================================

plugins {
    // Spring Boot plugin: provides bootRun (run the app) and bootJar (create fat JAR)
    id("org.springframework.boot")
    // Spring Dependency Management: imports Spring BOMs for consistent versions
    id("io.spring.dependency-management")
    // Standard Java plugin for compilation, test, and packaging tasks
    id("java")
}

val springCloudVersion: String by project

// Import Spring Boot and Spring Cloud BOMs (Bill of Materials)
// A BOM is a special POM that declares the versions of a set of related artifacts.
// By importing it, we don't need version numbers on Spring dependencies — the BOM
// provides compatible, tested-together versions automatically.
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

dependencies {
    // Eureka Server: transforms this Spring Boot app into a service registry
    // All microservices will register themselves here on startup
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")

    // Actuator: exposes /actuator/health, /actuator/info, /actuator/metrics
    // Health endpoint used by load balancers to determine if instance is alive
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Lombok for reduced boilerplate in any utility classes
    compileOnly("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")
    annotationProcessor("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
