import com.diffplug.gradle.spotless.SpotlessExtension
import java.util.Date

plugins {
    id("idea")
    id("java")
    id("gg.essential.loom") version "1.5.polyfrost.1"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.diffplug.spotless") version "6.11.0"
    id("io.freefair.lombok") version "8.2.2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.0.0"
}

val minecraft_version: String by project
val minecraft_version_range: String by project
val forge_version: String by project
val forge_version_range: String by project
val loader_version_range: String by project
val mapping_channel: String by project
val mapping_version: String by project
val mod_id: String by project
val mod_name: String by project
val mod_license: String by project
val mod_version: String by project
val mod_authors: String by project
val mod_description: String by project
val mod_group_id: String by project

val semaphore_base_version: String by project
val semaphore_version: String by project
val elementa_version: String by project
val uc_version: String by project

group = "${mod_group_id}.elementa"

version = "${mod_version}+${minecraft_version}"

base { archivesName.set(mod_id) }

loom {
    runs {
        named("client") {
            property("asmhelper.verbose", "true")
            programArgs("--username", "Dev")
            runDir = "run-client"
        }

        named("server") {
            property("asmhelper.verbose", "true")
            runDir = "run-server"
        }
    }
    forge { pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter()) }
}

kotlin { jvmToolchain(8) }

repositories {
    mavenCentral()
    maven { url = uri("https://repo.essential.gg/repository/maven-public") }
    maven {
        name = "singlerr's repo"
        url = uri("https://github.com/singlerr/mvn-repo/raw/maven2/")
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter { includeGroup("maven.modrinth") }
    }
    flatDir { dir("libs") }
}

sourceSets.main { output.setResourcesDir(file("$buildDir/classes/java/main")) }

val shadowImpl: Configuration by
    configurations.creating { configurations.modImplementation.get().extendsFrom(this) }

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("de.oceanlabs.mcp:mcp_${mapping_channel}:${mapping_version}")
    forge("net.minecraftforge:forge:${minecraft_version}-${forge_version}")

    shadowImpl("gg.essential:elementa:${elementa_version}")
    shadowImpl("gg.essential:universalcraft-${minecraft_version}-forge:$uc_version")

    implementation("io.github.singlerr.semaphore:policy-impl:${semaphore_version}")
    implementation("io.github.singlerr.semaphore:config:${semaphore_version}")
    implementation("io.github.singlerr.semaphore:screen-adapter:${semaphore_version}")
    implementation("io.github.singlerr.semaphore:sound-adapter:${semaphore_version}")

    implementation("io.github.singlerr.semaphore.callhandler:callhandler:${semaphore_base_version}")
    // DataGateways
    implementation(
        "io.github.singlerr.semaphore.datagateways:datagateways:${semaphore_base_version}"
    )
    // Interactors
    implementation("io.github.singlerr.semaphore.interactors:accessor:${semaphore_base_version}")
    implementation("io.github.singlerr.semaphore.interactors:admin:${semaphore_base_version}")
    implementation("io.github.singlerr.semaphore.interactors:callee:${semaphore_base_version}")
    implementation("io.github.singlerr.semaphore.interactors:caller:${semaphore_base_version}")
}

tasks.withType<JavaCompile> { options.encoding = "UTF-8" }

tasks.withType<Jar> {
    archiveBaseName.set(mod_id)
    manifest.attributes.run {
        this["Specification-Title"] = mod_name
        this["Specification-Vendor"] = mod_authors
        this["Specification-Version"] = "1"
        this["Implementation-Title"] = mod_name
        this["Implementation-Version"] = version
        this["Implementation-Vendor"] = mod_authors
        this["ForceLoadAsMod"] = "true"
        this["Implementation-Timestamp"] = Date()
        this["ForceLoadAsMod"] = "true"
    }
    exclude("META-INF/versions/*/")
}

tasks.named<ProcessResources>("processResources") {
    val replaceProperties =
        mapOf(
            "minecraft_version" to minecraft_version,
            "minecraft_version_range" to minecraft_version_range,
            "forge_version" to forge_version,
            "forge_version_range" to forge_version_range,
            "loader_version_range" to loader_version_range,
            "mod_id" to mod_id,
            "mod_name" to mod_id,
            "mod_license" to mod_license,
            "mod_version" to mod_version,
            "mod_authors" to mod_authors,
            "mod_description" to mod_description,
        )

    inputs.properties(replaceProperties)

    filesMatching(arrayListOf("mcmod.info", "pack.mcmeta")) {
        expand(replaceProperties + mapOf("project" to project))
    }
}

val remapJar by
    tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
        archiveClassifier.set("")
        from(tasks.shadowJar)
        input.set(tasks.shadowJar.get().archiveFile)
    }

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl)

    relocate("gg.essential.elementa", "${mod_group_id}.gg.essential.elementa")
    relocate("gg.essential.universal", "${mod_group_id}.gg.essential.universal")
    relocate("kotlin", "${mod_group_id}.kotlin")
    relocate("org.jetbrains", "${mod_group_id}.org.jetbrains")

    dependencies { exclude("META-INF/versions/**") }

    doLast { configurations.forEach { println("Copying jars into mod: ${it.files}") } }
}

configure<SpotlessExtension> {
    java {
        target("src/*/java/**/*.java", "*/src/*/java/**/*.java")
        palantirJavaFormat()
        licenseHeader("/* (C) \$YEAR singlerr */")
    }
    kotlinGradle {
        target("*.gradle.kts", "*/**.gradle.kts")

        ktfmt().kotlinlangStyle()
    }

    kotlin {
        target("src/*/kotlin/**/*.kt", "*/src/*/kotlin/**/*.kt")

        ktfmt().kotlinlangStyle()
    }
}
