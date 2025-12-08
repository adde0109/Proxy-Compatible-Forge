val mainCompileOnly: Configuration by configurations.getting
configurations.compileOnly.get().extendsFrom(mainCompileOnly)
val modImplementation: Configuration by configurations.creating

unimined.minecraft {
    version(minecraftVersion)
    neoForge {
        loader(neoforgeVersion)
    }
    mappings {
        parchment(parchmentMinecraft, parchmentVersion)
        mojmap()
        devFallbackNamespace("official")
    }
    defaultRemapJar = true
}

repositories {
    // Forgified Fabric API
    maven("https://maven.su5ed.dev/releases")
}

dependencies {
    compileOnly(project(":common"))
    modImplementation("org.sinytra.forgified-fabric-api:fabric-networking-api-v1:4.3.0+ab6ec1d119")
}
