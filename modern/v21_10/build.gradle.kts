val forge: SourceSet by sourceSets.creating
val neoforge: SourceSet by sourceSets.creating
val mainCompileOnly: Configuration by configurations.getting
val forgeCompileOnly: Configuration by configurations.getting {
    extendsFrom(mainCompileOnly)
}
val neoforgeCompileOnly: Configuration by configurations.getting {
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
    }
    defaultRemapJar = true
}

unimined.minecraft(neoforge) {
    combineWith(sourceSets.main.get())
    neoForge {
        loader(neoforgeVersion)
        accessTransformer(aw2at(rootProject.file("common/src/main/resources/accessWidener.aw")))
    }
    defaultRemapJar = true
}

dependencies {
    mainCompileOnly(libs.taterlib.lite.base)
    mainCompileOnly(libs.taterlib.lite.core)
    mainCompileOnly(libs.taterlib.lite.metadata)
    mainCompileOnly(libs.taterlib.lite.muxins)
    forgeCompileOnly(srcSetAsDep(":modern:v16_5", "forge"))
    forgeCompileOnly(srcSetAsDep(":modern:v17_1", "forge"))
    forgeCompileOnly(project(":modern:v20_2"))
    forgeCompileOnly(project(":common"))
    neoforgeCompileOnly(project(":modern:v20_2"))
    neoforgeCompileOnly(project(":common"))
}

tasks.jar {
    from(forge.output, neoforge.output)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
