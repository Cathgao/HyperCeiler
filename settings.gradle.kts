@file:Suppress("UnstableApiUsage")

data class GprCredentials(val user: String, val key: String)

fun loadGprCredentials(): GprCredentials {
    // 优先从环境变量读取
    val envUser = System.getenv("GIT_ACTOR")?.takeIf { it.isNotBlank() }
    val envKey = System.getenv("GIT_TOKEN")?.takeIf { it.isNotBlank() }

    if (envUser != null && envKey != null) {
        return GprCredentials(envUser, envKey)
    }

    // 从 signing.properties 读取
    val propsFile = File(rootDir, "signing.properties")
    val props = if (propsFile.exists()) {
        java.util.Properties().apply {
            propsFile.inputStream().use { load(it) }
        }
    } else {
        java.util.Properties()
    }

    val user = props.getProperty("gpr.user")?.takeIf { it.isNotBlank() } ?: "anonymous"
    val key = props.getProperty("gpr.key")?.takeIf { it.isNotBlank() } ?: "anonymous"

    return GprCredentials(user, key)
}

val gprCredentials by lazy { loadGprCredentials() }

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.github.com/ReChronoRain/HyperCeiler") {
            credentials {
                username = gprCredentials.user
                password = gprCredentials.key
            }
        }
        maven("https://jitpack.io")
        maven("https://api.xposed.info")
    }
}

rootProject.name = "HyperCeiler"

include(
    "app",
    // ":library:hook",
    ":library:libhook",
    // ":library:xposed-api-101",
    ":library:core",
    ":library:provision",
    ":library:common",
    ":library:processor",
    ":library:hidden-api",
)
