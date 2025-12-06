import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

subprojects {
    base {
        archivesName = "${modId}-${minecraftVersion}"
    }

    java.toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    java.sourceCompatibility = JavaVersion.toVersion(javaVersion)
    java.targetCompatibility = JavaVersion.toVersion(javaVersion)

    tasks.withType<RemapJarTask>().configureEach {
        mixinRemap {
            enableBaseMixin()
            disableRefmap()
        }
        archiveClassifier = "remapped"
    }

    var mainCompileOnly = configurations.maybeCreate("mainCompileOnly")

    dependencies {
        mainCompileOnly(libs.taterlib.lite.base)
        mainCompileOnly(libs.taterlib.lite.core)
        mainCompileOnly(libs.taterlib.lite.metadata)
        mainCompileOnly(libs.taterlib.lite.muxins)
    }
}
