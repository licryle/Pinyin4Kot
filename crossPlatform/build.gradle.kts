plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

group = "fr.berliat.pinyin4kot"
version = "1.2.0"

kotlin {
    jvm { }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.runtime)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }

        val jvmTest by getting
    }
}

// Ensure all Kotlin JVM compilations target Java 17
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

// Generate a JVM JAR including commonMain resources
tasks.named<Jar>("jvmJar") {
    archiveBaseName.set("pinyin4kot")
    archiveVersion.set(version.toString())

    from(kotlin.targets.getByName("jvm").compilations.getByName("main").output)
    from("src/commonMain/resources") // include shared resources

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.findByName("allTests") ?: tasks.register("allTests") {
    group = "verification"
    description = "Run all JVM and iOS tests"

    dependsOn(
        "jvmTest",
        "iosX64Test",
        "iosSimulatorArm64Test",
        "iosArm64Test"
    )
}

tasks.register("test") {
    group = "verification"
    description = "Run JVM tests for commonTest"

    dependsOn("jvmTest")
}
