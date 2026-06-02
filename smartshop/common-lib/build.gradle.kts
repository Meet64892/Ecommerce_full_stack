/*
 * common-lib is a shared Java library rather than a Spring Boot application.
 * The java-library plugin exposes a clean API/implementation separation and avoids creating
 * an executable fat jar, which would be unnecessary for code that is imported by services.
 */
plugins {
    `java-library`
}

val jacksonVersion: String by project

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
}
