import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

group = "fr.berliat.pinyin4kot"
version = "1.2.0"

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
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
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting
        val jvmTest by getting

        val androidMain by getting

        val iosMain by getting
        val iosTest by getting

        val macosMain by getting
        val macosTest by getting
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
