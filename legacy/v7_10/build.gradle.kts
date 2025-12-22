val forge: SourceSet by sourceSets.creating
val mainCompileOnly: Configuration by configurations.getting
configurations.compileOnly.get().extendsFrom(mainCompileOnly)
val forgeCompileOnly: Configuration by configurations.getting {
    extendsFrom(mainCompileOnly)
}

unimined.minecraft {
    version(minecraftVersion)
    mappings {
        searge()
        mcp(mcpChannel, mcpVersion)
    }
    defaultRemapJar = false
}

unimined.minecraft(forge) {
    combineWith(sourceSets.main.get())
    minecraftForge {
        loader(forgeVersion)
        mixinConfig("$modId.mixins.v7_10.forge.json")
        accessTransformer(aw2at(rootProject.file("common/src/main/resources/accessWidener.aw")))
    }
    defaultRemapJar = true
}

dependencies {
    forgeCompileOnly(libs.mixin)
    forgeCompileOnly(srcSetAsDep(":legacy:v12_2", "forge"))
    forgeCompileOnly(project(":common"))
}

tasks.jar {
    dependsOn("remapForgeJar")
    from(jarToFiles("remapForgeJar"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
