dependencies {
    compileOnly(project(":common"))
}

unimined.minecraft {
    version(minecraftVersion)
    minecraftForge {
        loader(forgeVersion)
        mixinConfig("pcf.mixins.v1_14_4.forge.json")
    }
    mappings {
        parchment(parchmentMinecraft, parchmentVersion)
        mojmap()
        devFallbackNamespace("official")
    }
    defaultRemapJar = false
    remap(tasks.jar.get()) {
        prodNamespace("searge")
        mixinRemap {
            disableRefmap()
        }
        archiveClassifier = "remapped"
    }
}
