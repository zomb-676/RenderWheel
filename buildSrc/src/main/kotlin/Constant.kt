import org.gradle.api.Project

//Mod options
const val mod_name = "ParticleStorm"
const val mod_author = "zomb"
const val mod_id = "particle_storm"

//Common
const val minecraft_version = "1.19.3"
const val parchment_version = "1.19.3:2022.12.18"
const val enabled_platforms = "fabric,forge"

//Fabric
const val fabric_loader_version = "0.14.9"
const val fabric_api_version = "0.73.0+$minecraft_version"
//const val cloth_config_version = "8.2.88"

//Forge
private const val forge_internal_version = "44.1.8"
const val forge_version = "$minecraft_version-$forge_internal_version"

//Project
private const val version_major = 0.1
private const val version_patch = 1
const val short_version = "$version_major.$version_patch"
const val semantics_version = "$minecraft_version-$short_version"

private const val mixinExtrasVersion = "0.1.0"
const val mixinExtras = "com.github.LlamaLad7:MixinExtras:$mixinExtrasVersion"

val Project.archiveBaseName get() = "$mod_name-${project.name.toLowerCase()}"

val jomlVersion = "1.10.5"
