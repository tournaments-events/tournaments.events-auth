plugins {
    id("io.micronaut.application")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":data-postgresql"))

    // Logging
    runtimeOnly("ch.qos.logback:logback-classic")

    // YAML: for configuration
    runtimeOnly("org.yaml:snakeyaml")
}

application {
    mainClass.set("com.sympauthy.Application")
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.sympauthy.*")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

graalvmNative {
    toolchainDetection.set(true)
    metadataRepository {
        enabled = true
    }
}
