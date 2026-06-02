import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.plugins.JavaPluginExtension

/*
 * Root build file for SmartShop.
 *
 * A root build file gives the repository one place for shared concerns: Java toolchains,
 * repositories, test defaults, BOM imports, and dependency versions. Individual services
 * still own their concrete dependencies so the gateway is not forced to carry JPA and the
 * notification service is not forced to carry a database driver.
 *
 * Kotlin DSL vs Groovy DSL: Gradle supports both build.gradle (Groovy) and build.gradle.kts
 * (Kotlin). Kotlin DSL gives type-safe accessors, IDE completion, and earlier feedback when
 * a task or extension name is wrong; Groovy DSL is more dynamic and historically older.
 *
 * allprojects {} applies configuration to the root project and every child. subprojects {}
 * applies only to child modules, which is what we want for Java conventions because the root
 * project is an aggregator and should not compile source code of its own.
 */
plugins {
    id("org.springframework.boot") version "3.2.3" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    id("java-library") apply false
}

val springCloudVersion: String by project
val springBootVersion: String by project
val lombokVersion: String by project
val mapstructVersion: String by project
val javaVersion: String by project

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    group = "com.smartshop"
    version = "1.0.0"

    extensions.configure<JavaPluginExtension>("java") {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
        }
    }

    extensions.configure<DependencyManagementExtension>("dependencyManagement") {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        }
    }

    configurations.configureEach {
        exclude(group = "commons-logging", module = "commons-logging")
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:$lombokVersion")
        annotationProcessor("org.projectlombok:lombok:$lombokVersion")
        annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-parameters"))
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
