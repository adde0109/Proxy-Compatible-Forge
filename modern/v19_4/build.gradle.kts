unimined.minecraft {
    version(minecraftVersion)
    minecraftForge {
        loader(forgeVersion)
        mixinConfig("pcf.mixins.v1_19_4.forge.json")
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

dependencies {
    compileOnly(project(":v14_4"))
    compileOnly(project(":v17_1"))
    compileOnly(project(":v19_2"))
    compileOnly(project(":v20_4"))
    compileOnly(project(":common"))
}
