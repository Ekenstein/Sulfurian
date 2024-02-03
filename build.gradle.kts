import com.github.benmanes.gradle.versions.updates.resolutionstrategy.ComponentFilter
import com.github.benmanes.gradle.versions.updates.resolutionstrategy.ComponentSelectionWithCurrent
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.inputStream

plugins {
    kotlin("jvm") version "1.9.22"
    application
    id("com.github.ben-manes.versions") version "0.51.0"
}

group = "com.github.ekenstein"
version = "1.0-SNAPSHOT"

val ktorVersion: String by project
val exposedVersion: String by project
val kotlinJvmTarget: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor", "ktor-server-auth", ktorVersion)
    implementation("io.ktor", "ktor-server-core", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)

    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("org.xerial", "sqlite-jdbc", "3.45.1.0")


    implementation("org.jetbrains.kotlinx", "kotlinx-cli", "0.3.6")

    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }

    dependencyUpdates {
        rejectVersionIf {
            UpgradeToUnstableFilter().reject(this) || IgnoredDependencyFilter().reject(this)
        }
    }

    val dependencyUpdateSentinel = register<DependencyUpdateSentinel>("dependencyUpdateSentinel", buildDir)
    dependencyUpdateSentinel.configure {
        dependsOn(dependencyUpdates)
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = kotlinJvmTarget
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = kotlinJvmTarget
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
        }
    }

    listOf(compileJava, compileTestJava).map { task ->
        task {
            sourceCompatibility = kotlinJvmTarget
            targetCompatibility = kotlinJvmTarget
        }
    }
}

application {
    mainClass.set("MainKt")
}

class IgnoredDependencyFilter : ComponentFilter {
    private val ignoredDependencies = mapOf(
        "ktlint" to listOf("0.46.0", "0.46.1") // doesn't currently work.
    )

    override fun reject(p0: ComponentSelectionWithCurrent): Boolean {
        return ignoredDependencies[p0.candidate.module].orEmpty().contains(p0.candidate.version)
    }
}

class UpgradeToUnstableFilter : ComponentFilter {
    override fun reject(cs: ComponentSelectionWithCurrent): Boolean {
        return reject(cs.currentVersion, cs.candidate.version)
    }

    private fun reject(old: String, new: String): Boolean {
        return !isStable(new) && isStable(old) // no unstable proposals for stable dependencies
    }

    private fun isStable(version: String): Boolean {
        val stableKeyword = setOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
        val stablePattern = version.matches(Regex("""^[0-9,.v-]+(-r)?$"""))
        return stableKeyword || stablePattern
    }
}

abstract class DependencyUpdateSentinel @Inject constructor(private val buildDir: File) : DefaultTask() {
    @ExperimentalPathApi
    @TaskAction
    fun check() {
        val updateIndicator = "The following dependencies have later milestone versions:"
        val report = Paths.get(buildDir.toString(), "dependencyUpdates", "report.txt")

        report.inputStream().bufferedReader().use { reader ->
            if (reader.lines().anyMatch { it == updateIndicator }) {
                throw GradleException("Dependency updates are available.")
            }
        }
    }
}