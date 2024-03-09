import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.allopen")
    id("com.google.devtools.ksp")
    id("io.micronaut.application")
}

dependencies {
    implementation(project(":data"))

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:${project.extra["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${project.extra["kotlinVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.extra["kotlinCoroutinesVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:${project.extra["kotlinCoroutinesVersion"]}")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")

    // Micronaut
    implementation("io.micronaut:micronaut-runtime")
    ksp("io.micronaut:micronaut-inject-java")

    // HTTP server
    ksp("io.micronaut.jaxrs:micronaut-jaxrs-processor")
    implementation("io.micronaut:micronaut-http-client")

    // HTTP client
    implementation("io.micronaut:micronaut-http-client")

    // Validation
    ksp("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut.validation:micronaut-validation")

    // Security
    ksp("io.micronaut.security:micronaut-security-annotations")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.security:micronaut-security-oauth2")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("com.auth0:java-jwt:${project.extra["javaJwtVersion"]}")
    implementation("org.bouncycastle:bcprov-jdk18on:${project.extra["bouncyCastleVersion"]}")

    // Reactive programming
    implementation("io.micronaut.rxjava3:micronaut-rxjava3")
    implementation("io.micronaut.rxjava3:micronaut-rxjava3-http-client")

    // Database
    implementation("io.micronaut.data:micronaut-data-r2dbc")
    implementation("jakarta.persistence:jakarta.persistence-api:3.0.0")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

    // Views
    implementation("io.micronaut.views:micronaut-views-thymeleaf")

    // Object mapping
    api("org.mapstruct:mapstruct:${project.extra["mapStructVersion"]}")
    kapt("org.mapstruct:mapstruct-processor:${project.extra["mapStructVersion"]}")

    // Serialization/Deserialization
    ksp("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")

    // Logging
    runtimeOnly("ch.qos.logback:logback-classic")

    // API documentation
    // Must be above 6.3.0 to fix KSP issue: https://github.com/micronaut-projects/micronaut-openapi/issues/1154
    ksp("io.micronaut.openapi:micronaut-openapi:6.3.0!!")
    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations:6.3.0!!")

    // YAML: for configuration
    runtimeOnly("org.yaml:snakeyaml")

    // JsonPath: for user info extraction
    implementation("com.jayway.jsonpath:json-path:${project.extra["jsonPathVersion"]}")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:${project.extra["junitJupiterVersion"]}")
    testImplementation("io.mockk:mockk:${project.extra["mockkVersion"]}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${project.extra["kotlinCoroutinesVersion"]}")
}

application {
    mainClass.set("com.sympauthy.Application")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.sympauthy.*")
    }
}

graalvmNative {
    toolchainDetection.set(true)
    metadataRepository {
        enabled = true
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "21"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "21"
        }
    }
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events(
                PASSED, SKIPPED, FAILED, STANDARD_ERROR, STANDARD_OUT
            )
        }
    }
    withType<Jar>() {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
