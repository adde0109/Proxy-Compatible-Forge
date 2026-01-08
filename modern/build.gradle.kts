import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

subprojects {
    apply(plugin = rootProject.libs.plugins.unimined.get().pluginId)

    base {
        archivesName = "${modId}-${minecraftVersion}"
    }

    tasks.withType<RemapJarTask>().configureEach {
        mixinRemap {
            enableBaseMixin()
            disableRefmap()
        }
    }

    val mainCompileOnly: Configuration by configurations.getting

    dependencies {
        listOf(
            project(":common")
        ).forEach {
            mainCompileOnly(it)
        }
    }
}
