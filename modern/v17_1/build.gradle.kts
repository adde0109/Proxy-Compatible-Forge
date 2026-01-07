val forge: SourceSet by sourceSets.creating
val mainCompileOnly: Configuration by configurations.getting
val forgeCompileOnly: Configuration by configurations.getting {
    extendsFrom(mainCompileOnly)
}

unimined.minecraft(sourceSets.main.get()) {
    version(minecraftVersion)
    mappings {
        parchment(parchmentMinecraft, parchmentVersion)
        mojmap()
        devFallbackNamespace("official")
    }
    defaultRemapJar = false
}

unimined.minecraft(forge) {
    combineWith(sourceSets.main.get())
    minecraftForge {
        loader(forgeVersion)
        mixinConfig("$modId.mixins.v17_1.forge.json")
        accessTransformer(aw2at(rootProject.file("common/src/main/resources/accessWidener.aw")))
    }
    defaultRemapJar = true
}

dependencies {
    forgeCompileOnly(srcSetAsDep(":modern:v16_5", "forge"))
    forgeCompileOnly(project(":common"))
}

tasks.jar {
    dependsOn("remapForgeJar")
    from(jarToFiles("remapForgeJar"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
