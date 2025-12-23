val forge: SourceSet by sourceSets.creating
val mainCompileOnly: Configuration by configurations.getting
configurations.compileOnly.get().extendsFrom(mainCompileOnly)
val forgeCompileOnly: Configuration by configurations.getting {
    extendsFrom(mainCompileOnly)
}

unimined.minecraft {
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
        mixinConfig("$modId.mixins.v19_4.forge.json")
        accessTransformer(aw2at(rootProject.file("common/src/main/resources/accessWidener.aw")))
    }
    defaultRemapJar = true
}

dependencies {
    evaluationDependsOn(":modern:v20_4")
    forgeCompileOnly(srcSetAsDep(":modern:v14_4", "forge"))
    forgeCompileOnly(srcSetAsDep(":modern:v17_1", "forge"))
    forgeCompileOnly(srcSetAsDep(":modern:v19_2", "forge"))
    forgeCompileOnly(srcSetAsDep(":modern:v20_4", "forge"))
    forgeCompileOnly(project(":common"))
}

tasks.jar {
    dependsOn("remapForgeJar")
    from(jarToFiles("remapForgeJar"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
