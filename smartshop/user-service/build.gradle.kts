// =============================================================================
// user-service/build.gradle.kts — User & Authentication Service Build Config
// =============================================================================

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
}

val springCloudVersion: String by project
val lombokVersion: String by rootProject.extra
val mapstructVersion: String by rootProject.extra
val lombokMapstructBindingVersion: String by rootProject.extra
val jjwtVersion: String by rootProject.extra
val springdocVersion: String by rootProject.extra

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

dependencies {
    // --- Shared Library ---
    // Depends on our common-lib for ApiResponse, ErrorResponse, BaseException, etc.
    // Gradle resolves this as a project dependency (compiled and included at build time)
    implementation(project(":common-lib"))

    // --- Web Layer ---
    implementation("org.springframework.boot:spring-boot-starter-web")

    // --- Data Layer ---
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.flywaydb:flyway-core:10.8.1")
    // Flyway PostgreSQL database support (separate from flyway-core since Flyway 10)
    implementation("org.flywaydb:flyway-database-postgresql:10.8.1")

    // --- Security ---
    implementation("org.springframework.boot:spring-boot-starter-security")
    // JJWT API: defines the interfaces and fluent builder for JWT creation/parsing
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    // JJWT implementations: the actual HMAC/RSA signing algorithms (runtime only)
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    // JJWT Jackson: JSON serialization of JWT claims (runtime only — loaded via ServiceLoader)
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    // --- Validation ---
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // --- Service Discovery ---
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // --- Centralized Config ---
    implementation("org.springframework.cloud:spring-cloud-starter-config")

    // --- Observability ---
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    // --- API Documentation ---
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    // --- Code Generation: Lombok ---
    // compileOnly: Lombok is a compile-time processor — it generates boilerplate
    // Java code (getters, setters, constructors) during compilation.
    // The generated code is in the bytecode; Lombok itself is NOT needed at runtime.
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // --- Code Generation: MapStruct ---
    // MapStruct generates type-safe mapper implementations at compile time.
    // WHY MapStruct over ModelMapper (reflection-based)?
    //   - MapStruct generates plain Java code (fast, no reflection overhead)
    //   - Compile-time errors (not runtime) if mappings are incomplete
    //   - IDE can navigate from interface to generated implementation
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    // Lombok-MapStruct binding: Lombok runs BEFORE MapStruct so MapStruct can
    // see Lombok-generated getters/setters. Without this, MapStruct can't find
    // getters and generates empty/incorrect mappers.
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$lombokMapstructBindingVersion")

    // --- Testing ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2")   // In-memory DB for tests (no PostgreSQL needed)
}

// Configure the annotation processor ordering:
// Lombok MUST run before MapStruct, otherwise MapStruct sees classes WITHOUT
// Lombok-generated getters/setters and fails to generate correct mappers.
tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        "-Amapstruct.suppressGeneratorTimestamp=true",   // Don't embed timestamp in generated code
        "-Amapstruct.defaultComponentModel=spring"        // Generate @Component on mapper implementations
    ))
}
