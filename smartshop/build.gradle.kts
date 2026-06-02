/*
 * =============================================================================
 * build.gradle.kts (ROOT) — Shared build configuration for every sub-project
 * =============================================================================
 *
 * WHY A ROOT BUILD FILE?
 * ----------------------
 * In a multi-module build we want every module to share the same Java version,
 * the same group/version, the same repositories, and the same common
 * dependencies (Lombok, test libraries, the Spring Cloud BOM, ...). Copy-pasting
 * that into 9 separate build files would be a maintenance nightmare. Instead we
 * configure it ONCE here and let it cascade down.
 *
 * KOTLIN DSL vs GROOVY DSL
 * ------------------------
 * Gradle build scripts can be written in Groovy (build.gradle) or Kotlin
 * (build.gradle.kts). We use Kotlin DSL because:
 *   * It is statically typed — your IDE autocompletes tasks/extensions and
 *     catches typos at edit time instead of at build time.
 *   * Navigation ("go to definition") works into the Gradle API.
 *   * Refactoring is safer.
 * The trade-off is slightly slower first-time script compilation, which is
 * negligible for a project of this size.
 *
 * allprojects {} vs subprojects {}
 * --------------------------------
 *   * allprojects {}  — configuration applied to the ROOT project AND every
 *                       sub-project. Use for things truly universal (group,
 *                       version, repositories).
 *   * subprojects {}  — configuration applied to every sub-project but NOT the
 *                       root. Use for module-level concerns (the root has no
 *                       source code, so it should not get the Java plugin).
 * =============================================================================
 */

plugins {
    // We declare plugin versions here with `apply false` so the versions are
    // resolved/aligned at the root, but the plugins are actually *applied*
    // inside each sub-project that needs them. This avoids applying the
    // Spring Boot plugin to modules (like common-lib) that are plain libraries.
    java
    id("org.springframework.boot") version "3.2.3" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
}

// Applied to root + every module: identity + where to fetch artifacts.
allprojects {
    group = "com.smartshop"
    version = "1.0.0"

    repositories {
        // Maven Central hosts the vast majority of Java OSS artifacts.
        mavenCentral()
    }
}

// Applied to every module EXCEPT the (codeless) root project.
subprojects {
    // Every module is a Java module. The base `java` plugin gives us the
    // `compileJava`, `test`, `jar`, etc. tasks.
    apply(plugin = "java")
    // Lets each module import a BOM and get managed (version-less) dependencies.
    apply(plugin = "io.spring.dependency-management")

    // Pin the language level for the whole platform from gradle.properties so
    // a single edit changes every module at once.
    java {
        val javaVersion = project.property("javaVersion").toString()
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }

    // Read centralized versions once so they can be reused below.
    val springCloudVersion: String by project
    val lombokVersion: String by project
    val mapstructVersion: String by project

    // dependencyManagement imports BOMs. A BOM ("Bill of Materials") is a
    // special POM that only declares <dependencyManagement> versions. Importing
    // it means we can later write `implementation("group:artifact")` WITHOUT a
    // version and Gradle resolves the version the BOM dictates — keeping the
    // whole transitive graph internally consistent.
    extensions.configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            // The Spring Cloud release train BOM (aligns Gateway, Eureka, Config,
            // Resilience4j, Micrometer Tracing bridge, etc.).
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        }
    }

    // Dependencies EVERY module shares.
    dependencies {
        // Lombok: compile-time code generation for getters/builders/@Slf4j.
        "compileOnly"("org.projectlombok:lombok:$lombokVersion")
        "annotationProcessor"("org.projectlombok:lombok:$lombokVersion")
        "testCompileOnly"("org.projectlombok:lombok:$lombokVersion")
        "testAnnotationProcessor"("org.projectlombok:lombok:$lombokVersion")

        // MapStruct: generates mapper implementations during compilation.
        "implementation"("org.mapstruct:mapstruct:$mapstructVersion")
        "annotationProcessor"("org.mapstruct:mapstruct-processor:$mapstructVersion")
        // This binding lets MapStruct understand Lombok-generated accessors when
        // both annotation processors run together.
        "annotationProcessor"("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    }

    // Use the modern JUnit 5 platform for all module tests.
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
