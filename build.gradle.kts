buildscript {
    val kotlinVersion = project.findProperty("kotlinVersion")
    val micronautVersion = project.findProperty("micronautVersion")

    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin.kapt:org.jetbrains.kotlin.kapt.gradle.plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin.plugin.allopen:org.jetbrains.kotlin.plugin.allopen.gradle.plugin:$kotlinVersion")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$kotlinVersion-1.0.19")
        classpath("io.micronaut.gradle:micronaut-gradle-plugin:$micronautVersion")
    }
}

allprojects {
    apply<JavaPlugin>()

    version = "0.1"
    group = "com.sympauthy"

    extra.apply {
        set("kotlinVersion", project.findProperty("kotlinVersion"))
        set("kotlinCoroutinesVersion", "1.8.0")
        set("mapStructVersion", "1.5.1.Final")
        set("javaJwtVersion", "4.4.0")
        set("jsonPathVersion", "2.8.0")
        set("bouncyCastleVersion", "1.77")

        // Test dependencies
        set("junitJupiterVersion", "5.10.1")
        set("mockkVersion", "1.13.8")
    }

    repositories {
        mavenCentral()
    }
}
