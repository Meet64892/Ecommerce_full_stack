plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":common-lib"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:${project.property("resilience4jVersion")}")
    implementation("io.micrometer:micrometer-tracing-bridge-brave:${project.property("micrometerTracingVersion")}")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave:${project.property("zipkinReporterVersion")}")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:${project.property("springdocVersion")}")
}
