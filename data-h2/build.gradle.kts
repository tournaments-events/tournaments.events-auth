import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.devtools.ksp")
    id("io.micronaut.library")
}

dependencies {
    api(project(":common"))
    api(project(":data"))

    // Kotlin
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${project.extra["kotlinVersion"]}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.extra["kotlinCoroutinesVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${project.extra["kotlinVersion"]}")

    // Micronaut
    implementation("io.micronaut:micronaut-runtime")

    // Database
    api("io.micronaut.data:micronaut-data-r2dbc")
    api("io.r2dbc:r2dbc-h2")

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
