pluginManagement {
    repositories {
        mavenCentral()
        maven (  "https://maven.fabricmc.net/" )
        maven (  "https://maven.architectury.dev/" )
        maven (  "https://maven.minecraftforge.net/" )
        gradlePluginPortal()
    }
    plugins{
        id("com.github.johnrengelman.shadow").version("7.1.2").apply(false)
        id("architectury-plugin").version("3.4-SNAPSHOT").apply(false)
        id("dev.architectury.loom").version ("1.0-SNAPSHOT").apply(false)
        id("kotlin").apply(false)
        id("io.freefair.lombok").version("6.6.1").apply(false)
        id("io.github.juuxel.loom-quiltflower").version("1.8.0").apply(false)
    }
}

include("common")
include("fabric")
include("forge")
include("core")

rootProject.name = "ParticleStorm"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

