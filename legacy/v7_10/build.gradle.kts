val forge: SourceSet by sourceSets.creating
val mainCompileOnly: Configuration by configurations.getting
val forgeCompileOnly: Configuration by configurations.getting {
    extendsFrom(mainCompileOnly)
}

unimined.minecraft(sourceSets.main.get()) {
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
    forgeCompileOnly(project(":common"))
}

tasks.jar {
    dependsOn("remapForgeJar")
    from(jarToFiles("remapForgeJar"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
