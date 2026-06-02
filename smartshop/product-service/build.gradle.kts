plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":common-lib"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("io.micrometer:micrometer-tracing-bridge-brave:${project.property("micrometerTracingVersion")}")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave:${project.property("zipkinReporterVersion")}")
    implementation("org.flywaydb:flyway-core:${project.property("flywayVersion")}")
    runtimeOnly("org.postgresql:postgresql:${project.property("postgresqlVersion")}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${project.property("springdocVersion")}")
}
