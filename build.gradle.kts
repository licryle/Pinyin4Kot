import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

group = "fr.berliat.pinyin4kot"
version = "1.2.0"

kotlin {
    jvmToolchain(21)
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    android {
        namespace = "fr.berliat.pinyin4kot"
        compileSdk = 36
        minSdk = 26

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // iOS targets
    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())
    iosTargets.forEach { target ->
        target.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    // macOS targets
    val macosTargets = listOf(macosX64(), macosArm64())
    macosTargets.forEach { target ->
        target.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        getByName("commonMain")
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        getByName("jvmMain")
        getByName("jvmTest")

        getByName("androidMain")

        getByName("iosMain")
        getByName("iosTest")

        getByName("macosMain")
        getByName("macosTest")
    }
}

// Generate a JVM JAR including commonMain resources
tasks.named<Jar>("jvmJar") {
    archiveBaseName.set("pinyin4kot")
    archiveVersion.set(version.toString())

    from(kotlin.targets.getByName("jvm").compilations.getByName("main").output)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.findByName("allTests") ?: tasks.register("allTests") {
    group = "verification"
    description = "Run all JVM and iOS tests"

    dependsOn(kotlin.targets.map { it.compilations.getByName("test").compileTaskProvider })
}
