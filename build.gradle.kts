import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.kotlin.kapt") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.21"
    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "4.2.1"
    id("com.google.cloud.tools.jib") version "3.4.0"
}

version = "0.1"
group = "events.tournament"

extra.apply {
    set("kotlinVersion", "1.9.21")
    set("kotlinCoroutinesVersion", "1.7.3")
    set("mapStructVersion", "1.5.1.Final")
    set("komapperVersion", "1.15.0")
    set("r2dbcPostgres", "0.9.1.RELEASE")
    set("jacksonVersion", "2.13.4")
    set("jacksonKotlinVersion", "2.13.4")
    set("vertxJsonSchemaVersion", "4.3.1")
    set("jsonSchemaValidatorVersion", "1.0.73")
    set("mockitoVersion", "4.8.0")
    set("mockitoKotlinVersion", "4.0.0")
    set("junitJupiterVersion", "5.9.1")
    set("javaJwtVersion", "4.4.0")
}

repositories {
    mavenCentral()
}

dependencies {

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
    implementation("io.micronaut:micronaut-jackson-databind")

    // Validation
    ksp("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut.validation:micronaut-validation")

    // Security
    ksp("io.micronaut.security:micronaut-security-annotations")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.security:micronaut-security-oauth2")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("com.auth0:java-jwt:${project.extra["javaJwtVersion"]}")

    // Reactive programming
    implementation("io.micronaut.rxjava3:micronaut-rxjava3")
    implementation("io.micronaut.rxjava3:micronaut-rxjava3-http-client")

    // Database
    implementation("io.micronaut.data:micronaut-data-r2dbc")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

    // Views
    implementation("io.micronaut.views:micronaut-views-thymeleaf")

    // Object mapping
    api("org.mapstruct:mapstruct:${project.extra["mapStructVersion"]}")
    kapt("org.mapstruct:mapstruct-processor:${project.extra["mapStructVersion"]}")

    // Serialization/Deserialization
    kapt("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")

    // Logging
    runtimeOnly("ch.qos.logback:logback-classic")

    // API documentation
    // kapt("io.micronaut.configuration:micronaut-openapi:1.4.5")
    ksp("io.micronaut.openapi:micronaut-openapi")
    implementation("io.swagger.core.v3:swagger-annotations")

    // YAML: for configuration
    runtimeOnly("org.yaml:snakeyaml")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:${project.extra["junitJupiterVersion"]}")
    testImplementation("org.mockito:mockito-junit-jupiter:${project.extra["mockitoVersion"]}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${project.extra["mockitoKotlinVersion"]}")
}

application {
    mainClass.set("events.tournaments.code.Application")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "17"
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
    jib {
        to {
            image = "gcr.io/myapp/jib-image"
        }
    }
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("tournament.events.*")
    }
}
