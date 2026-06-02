/*
 * Root build.gradle.kts - shared build contract for every module
 *
 * Why we use a root build file:
 * - It enforces consistent compiler flags, repositories, BOM imports, and dependency versions.
 * - It prevents copy/paste divergence where one service drifts from the rest.
 *
 * Kotlin DSL vs Groovy DSL:
 * - Kotlin DSL is type-safe and IDE-friendly (autocomplete, refactor support, compile-time checks).
 * - Groovy DSL is dynamic and concise, but many build errors surface only at runtime.
 *
 * allprojects {} vs subprojects {}:
 * - allprojects {} configures the root + children.
 * - subprojects {} configures only child modules.
 * We use subprojects for shared service behavior and keep root-specific logic at top-level.
 */
plugins {
    id("org.springframework.boot") version "3.2.3" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    id("java")
}

val springCloudVersion: String by project
val javaVersion: String by project
val mapstructVersion: String by project
val lombokVersion: String by project

allprojects {
    group = "com.smartshop"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }

    dependencies {
        "compileOnly"("org.projectlombok:lombok:$lombokVersion")
        "annotationProcessor"("org.projectlombok:lombok:$lombokVersion")
        "implementation"("org.mapstruct:mapstruct:$mapstructVersion")
        "annotationProcessor"("org.mapstruct:mapstruct-processor:$mapstructVersion")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
