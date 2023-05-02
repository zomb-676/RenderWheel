import juuxel.loomquiltflower.api.LoomQuiltflowerPlugin
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("architectury-plugin")
    id("dev.architectury.loom").apply(false)
    id("io.github.juuxel.loom-quiltflower").apply(false)

}

architectury {
    minecraft = minecraft_version
}

subprojects {
    if (this.name == "core") return@subprojects

    apply(plugin = "dev.architectury.loom")
    apply<LoomQuiltflowerPlugin>()

    val loom = extensions.getByType<LoomGradleExtensionAPI>()
    loom.run {
        silentMojangMappingsLicense()
    }

    repositories {
        mavenLocal()
        maven {
            url = uri("https://maven.parchmentmc.org/")
            content {
                includeGroup("org.parchmentmc.data")
            }
        }
        maven {
            url = uri("https://cursemaven.com")
            content {
                includeGroup("curse.maven")
            }
        }
        maven {
            name = "Modrinth"
            url = uri("https://api.modrinth.com/maven")
            content {
                includeGroup("maven.modrinth")
            }
        }
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:$minecraft_version")
        // The following line declares the mojmap mappings, you may use other mappings as well
        "mappings"(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-$parchment_version@zip")
        })
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")

    version = semantics_version

    tasks.withType<JavaCompile>{
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    extensions.getByType<JavaPluginExtension>().apply {
        withSourcesJar()
    }

}