pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.polyfrost.cc/releases/")
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://maven.architectury.dev")
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net")
    }
    // We also recommend specifying your desired version here if you're using more than one of the
    // plugins,
    // so you do not have to change the version in multilpe places when updating.
    plugins {
        val egtVersion = "0.1.0" // should be whatever is displayed in above badge
        id("gg.essential.multi-version.root") version egtVersion
        id("gg.essential.multi-version.api-validation") version egtVersion
        id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
    }
}

rootProject.name = "semaphore-elementa"
