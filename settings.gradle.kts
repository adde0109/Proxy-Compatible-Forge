pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()

        // NeuralNexus Mirror
        maven("https://maven.neuralnexus.dev/mirror")

        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases")
        maven("https://repo.spongepowered.org/maven")

        // Unimined
        maven("https://maven.wagyourtail.xyz/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.5.0")
}

rootProject.name = "proxy-compatible-forge"

include(":common")
include(":modern")
val modernVersions = listOf(
    "14_4",
    "16_5",
    "17_1",
    "18_2",
    "19_2",
    "19_4",
    "20_2",
    "20_4",
    "21_10"
).forEach { version ->
    include(":modern:v$version")
}
