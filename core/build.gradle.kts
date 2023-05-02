import org.jetbrains.kotlin.cli.jvm.main
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.distsDirectory
import org.lwjgl.Lwjgl.Module
import org.lwjgl.lwjgl

plugins {
    java
    kotlin("jvm").version("1.8.0")
    id("io.freefair.lombok")
    id("org.lwjgl.plugin") version "0.0.30"
    application
}

application {
    mainClass.set("Main")
    executableDir = rootProject.rootProject.parent.toString()
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
        content {
            includeGroup("systems.manifold")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")

    lwjgl {
        release.latest
        implementation(Module.glfw, Module.opengl, Module.stb, Module.vulkan, Module.nanovg)
    }

    //logger
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")

    implementation("org.joml", "joml", jomlVersion)
    implementation("org.apache.httpcomponents:httpcore:4.4.16")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("it.unimi.dsi:fastutil:8.5.11")
    implementation("net.java.dev.jna:jna:5.13.0")

}