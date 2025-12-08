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
        mixinConfig("$modId.mixins.v14_4.forge.json")
    }
    defaultRemapJar = true
}

dependencies {
    forgeCompileOnly(libs.mixin)
    forgeCompileOnly(project(":common"))
}

tasks.jar {
    dependsOn("remapForgeJar")
    from(jarToFiles("remapForgeJar"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
