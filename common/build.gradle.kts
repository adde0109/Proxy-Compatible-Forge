plugins {
    alias(libs.plugins.shadow)
}

base {
    archivesName = "$modId-common"
}

dependencies {
    compileOnly(libs.mojang.authlib)
    compileOnly(libs.guava)
    compileOnly(libs.mixin)
    compileOnly(libs.netty.buffer)
    compileOnly(libs.netty.codec)
    compileOnly(libs.asm.tree)

    compileOnly(libs.entrypoint.spoof)
    implementation(libs.taterlib.lite.base)
    implementation(libs.taterlib.lite.core)
    implementation(libs.taterlib.lite.metadata)
    implementation(libs.taterlib.lite.muxins)
}

tasks.withType<ProcessResources> {
    filesMatching(listOf(
            "META-INF/mods.toml",
            "META-INF/neoforge.mods.toml",
            "mcmod.info",
            "pack.mcmeta",
    )) {
        expand(project.properties)
    }
}

tasks.shadowJar {
    archiveClassifier = "shaded"
}

tasks.build.get().dependsOn(tasks.shadowJar)
