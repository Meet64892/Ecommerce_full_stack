// =============================================================================
// config-server/build.gradle.kts — Spring Cloud Config Server Build Config
// =============================================================================

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
}

val springCloudVersion: String by project

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

dependencies {
    // Config Server: the core dependency that enables @EnableConfigServer
    // Provides REST endpoints: GET /{application}/{profile} and /{application}/{profile}/{label}
    implementation("org.springframework.cloud:spring-cloud-config-server")

    // Eureka Client: config-server registers itself so other services can discover it.
    // This is optional but recommended — services can find the config server by name
    // rather than hardcoded URL. Note the chicken-and-egg issue: config-server must
    // start before other services but can still register with Eureka for discovery.
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // Actuator for health checks and metrics
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Security: in production, the config server should require authentication
    // because it serves sensitive config (DB passwords, JWT secrets, API keys)
    implementation("org.springframework.boot:spring-boot-starter-security")

    compileOnly("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")
    annotationProcessor("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}
