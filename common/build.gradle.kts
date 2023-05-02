plugins {
    id("io.freefair.lombok")
}

architectury {
    common(enabled_platforms.split(","))
}

loom {
    accessWidenerPath.set(file("src/main/resources/$mod_id.accesswidener"))
}

dependencies {
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation("net.fabricmc:fabric-loader:${fabric_loader_version}")

    modCompileOnly("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
}
