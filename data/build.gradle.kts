import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
    id("io.micronaut.library")
}

dependencies {
    api(project(":common"))

    // Kotlin
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${project.extra["kotlinVersion"]}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.extra["kotlinCoroutinesVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${project.extra["kotlinCoroutinesVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${project.extra["kotlinVersion"]}")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")

    // Micronaut
    implementation("io.micronaut:micronaut-runtime")

    // R2DBC Database
    api("io.micronaut.data:micronaut-data-r2dbc")
    api("jakarta.persistence:jakarta.persistence-api:3.0.0")
    ksp("io.micronaut.data:micronaut-data-processor")

    // Database migration
    api("io.micronaut.flyway:micronaut-flyway")

    // H2: R2DBC + JDBC for migration
    api("io.r2dbc:r2dbc-h2")

    // PostgreSQL: R2DBC + JDBC for migration
    api("org.postgresql:r2dbc-postgresql")
    api("org.flywaydb:flyway-database-postgresql")
    api("org.postgresql:postgresql")

    // Serialization/Deserialization
    ksp("io.micronaut.serde:micronaut-serde-processor")
    api("io.micronaut.serde:micronaut-serde-jackson")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:${project.extra["junitJupiterVersion"]}")
    testImplementation("io.mockk:mockk:${project.extra["mockkVersion"]}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${project.extra["kotlinCoroutinesVersion"]}")
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
