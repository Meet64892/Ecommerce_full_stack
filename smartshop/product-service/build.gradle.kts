// =============================================================================
// product-service/build.gradle.kts — Product Catalog Service Build Config
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
val springdocVersion: String by rootProject.extra

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

dependencies {
    implementation(project(":common-lib"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.flywaydb:flyway-core:10.8.1")
    implementation("org.flywaydb:flyway-database-postgresql:10.8.1")

    // Elasticsearch integration — provides Spring Data Elasticsearch repositories
    // and ElasticsearchTemplate for custom queries.
    // Spring Boot auto-configures a RestClient connecting to spring.elasticsearch.uris
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$lombokMapstructBindingVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        "-Amapstruct.suppressGeneratorTimestamp=true",
        "-Amapstruct.defaultComponentModel=spring"
    ))
}
