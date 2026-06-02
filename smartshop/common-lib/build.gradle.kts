// =============================================================================
// common-lib/build.gradle.kts — Shared Library Build Configuration
// =============================================================================
//
// WHY java-library AND NOT spring-boot?
// The `java-library` plugin is designed for libraries that are consumed by OTHER
// projects. It provides the `api` and `implementation` dependency configurations:
//   - `api`: Dependencies exposed to consumers of this library (in the compile classpath)
//   - `implementation`: Dependencies internal to this library (NOT exposed to consumers)
//
// If we used the `spring-boot` plugin here, Gradle would try to create a fat JAR
// (bootJar) with an embedded Tomcat server — useless for a shared library.
// The `spring-boot` plugin also disables the standard `jar` task in favor of `bootJar`,
// which would break module-to-module dependencies.
//
// DRY PRINCIPLE — Why a Shared Library?
// Every microservice needs:
//   - Consistent API response format (ApiResponse<T>)
//   - Standardized error responses (ErrorResponse)
//   - Common exception types (ResourceNotFoundException)
//   - Kafka event base class (traceability, correlation IDs)
// Without common-lib, each service would define its own version, leading to
// diverging formats and inconsistent error handling across the platform.
// =============================================================================

// Apply the java-library plugin (NOT spring-boot)
plugins {
    `java-library`
}

// Read version properties defined in the root gradle.properties
val lombokVersion: String by rootProject.extra
val mapstructVersion: String by rootProject.extra

dependencies {
    // --- Spring Web (for HttpStatus) ---
    // `api` scope: all consumers of common-lib (which are Spring Boot services) will
    // have spring-web on their classpath. BaseException uses HttpStatus to map to HTTP codes.
    api("org.springframework:spring-web:6.1.4")

    // --- Jackson for JSON serialization in DTOs and utilities ---
    // `api` scope: consumers of common-lib automatically get Jackson on their classpath
    // because our ApiResponse<T> uses Jackson annotations like @JsonInclude
    api("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")

    // --- Lombok: Reduces boilerplate (@Data, @Builder, @Slf4j) ---
    // `compileOnly`: Lombok is a compile-time annotation processor; it generates
    // bytecode at compile time and is NOT needed at runtime (no runtime overhead)
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    // `annotationProcessor`: Tells the Java compiler to run Lombok's processor
    // during compilation. Must appear in BOTH compileOnly and annotationProcessor.
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // --- Jakarta Validation API (Bean Validation 3.0) ---
    // `api` scope: services using common-lib DTOs need validation annotations
    // This is just the API (annotations); the implementation (Hibernate Validator)
    // is provided by spring-boot-starter-validation in each service
    api("jakarta.validation:jakarta.validation-api:3.0.2")

    // --- SLF4J for logging interface ---
    api("org.slf4j:slf4j-api:2.0.12")

    // --- Testing ---
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
}
