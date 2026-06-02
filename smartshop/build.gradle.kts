// =============================================================================
// build.gradle.kts (ROOT) — Shared Configuration for All Submodules
// =============================================================================
//
// WHY A ROOT BUILD FILE?
// In a multi-module project, every service needs the same basic setup:
//   - Java 21 compiler settings
//   - Lombok annotation processing
//   - Common test dependencies (JUnit 5)
//   - Repository declarations (Maven Central)
// Without a root build file, you'd copy-paste this into every single
// build.gradle.kts. The root build file uses `subprojects {}` and `allprojects {}`
// to apply configuration to every module from one central location.
//
// KOTLIN DSL vs GROOVY DSL:
// Gradle build scripts can be written in two languages:
//   - Groovy DSL (build.gradle): Dynamic, flexible but no IDE autocompletion
//   - Kotlin DSL (build.gradle.kts): Statically typed, full IDE support, refactoring
// We use Kotlin DSL (.kts) because:
//   1. Your IDE can autocomplete plugin and dependency names
//   2. Typos in dependency names cause compile errors, not silent runtime failures
//   3. Kotlin DSL is the modern standard recommended by Gradle since v6.0
//
// allprojects {} vs subprojects {}:
//   - allprojects {}: Applies to the ROOT project AND all submodules
//   - subprojects {}: Applies ONLY to submodules, not the root project itself
// Use allprojects for repository declarations (every project needs Maven Central).
// Use subprojects for build configuration (the root project has no source code).
// =============================================================================

// Import version properties defined in gradle.properties
// The `by project` delegate reads from the project's property map at runtime
val springBootVersion: String by project
val springCloudVersion: String by project
val javaVersion: String by project
val mapstructVersion: String by project
val lombokVersion: String by project
val lombokMapstructBindingVersion: String by project
val jjwtVersion: String by project
val springdocVersion: String by project
val testcontainersVersion: String by project

// =============================================================================
// PLUGINS BLOCK (Root-Level)
// Plugins declared here are available to subprojects but not automatically applied.
// We use `apply false` so that each subproject opts in explicitly, preventing
// the Spring Boot plugin from running on common-lib (which is not a Boot app).
// =============================================================================
plugins {
    // Spring Boot plugin: provides bootRun, bootJar tasks and dependency management
    // `apply false` = declare it as available but don't apply it to the root project
    id("org.springframework.boot") version "3.2.3" apply false

    // Spring Dependency Management: imports Spring BOMs so we don't need version
    // numbers on Spring dependencies — the BOM manages compatible versions for us
    id("io.spring.dependency-management") version "1.1.4" apply false

    // Kotlin JVM plugin (in case we ever add Kotlin source files to any module)
    kotlin("jvm") version "1.9.22" apply false
}

// =============================================================================
// allprojects {} — Applied to Root + Every Submodule
// =============================================================================
allprojects {
    // Maven repository declarations: where should Gradle download JARs from?
    // mavenCentral() is the standard public artifact repository
    // mavenLocal() checks your local ~/.m2 cache first (useful for local lib dev)
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

// =============================================================================
// subprojects {} — Applied to Every Submodule (not the root project itself)
// =============================================================================
subprojects {
    // Apply Java plugin to all submodules — provides compile, test, jar tasks
    apply(plugin = "java")

    // Configure Java toolchain: tells Gradle to use a specific JDK version
    // regardless of which JDK happens to be installed on the developer's machine.
    // This prevents "it compiled on Java 17 but needs Java 21 features" surprises.
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        // Generate source JARs so IntelliJ can show source when debugging dependencies
        withSourcesJar()
    }

    // Configure all Java compilation tasks
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"

        // Enable preview features (e.g., virtual threads, records patterns)
        // Java 21 preview features require explicit opt-in at compile AND runtime
        options.compilerArgs.addAll(listOf(
            // Annotation processor paths are set per-service in their own build files
            // because not every service needs the same processors
        ))
    }

    // Configure test tasks to use JUnit Platform (JUnit 5)
    // Without this, Gradle would use JUnit 4 runner and skip @Test methods
    tasks.withType<Test> {
        useJUnitPlatform()
        // Show test results in console during CI (not just in HTML report)
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    // =============================================================================
    // SHARED DEPENDENCY VERSIONS (accessible in all subprojects)
    // These extra properties are available as `extra["lombokVersion"]` in subprojects
    // =============================================================================
    extra["lombokVersion"] = lombokVersion
    extra["mapstructVersion"] = mapstructVersion
    extra["lombokMapstructBindingVersion"] = lombokMapstructBindingVersion
    extra["jjwtVersion"] = jjwtVersion
    extra["springdocVersion"] = springdocVersion
    extra["testcontainersVersion"] = testcontainersVersion
}
