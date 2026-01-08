val mainCompileOnly: Configuration by configurations.getting

plugins {
    id("org.spongepowered.gradle.vanilla") version("0.2.2")
    id("xyz.wagyourtail.unimined") apply(false)
}

minecraft {
    version(minecraftVersion)
    accessWideners(rootProject.file("common/src/main/resources/accessWidener.aw"))
}

dependencies {
    mainCompileOnly(libs.asm.tree)
    mainCompileOnly(libs.mixin)
    mainCompileOnly(project(":modern:v20_2"))
}

tasks.jar {
    from(sourceSets.main.get().output)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
