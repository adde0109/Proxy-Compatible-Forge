import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.ArrayList

abstract class MergeMixinConfigs : DefaultTask() {

    @get:InputFiles
    abstract val inputFiles: ListProperty<RegularFile>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Input
    abstract val config: MapProperty<String, Any>

    @get:Input
    abstract val match: Property<String>

    @TaskAction
    fun merge() {
        val mixinConfigs = mutableListOf<File>()

        inputFiles.get().forEach { rf ->
            project.zipTree(rf.asFile).matching {
                include(match.get())
            }.files.forEach { file ->
                mixinConfigs.add(file)
            }
        }

        val out = outputFile.get().asFile
        out.parentFile.mkdirs()

        val json = JsonSlurper()
        val mixins = ArrayList<String>()
        val client = ArrayList<String>()
        val server = ArrayList<String>()

        mixinConfigs.forEach { mixinConfig ->
            val parsed = json.parse(mixinConfig) as Map<String, Collection<String>>
            mixins.addAll(parsed["mixins"] ?: emptyList())
            client.addAll(parsed["client"] ?: emptyList())
            server.addAll(parsed["server"] ?: emptyList())
        }

        val config = config.get().toMutableMap()
        val configMixins = config["mixins"] as? Collection<String> ?: emptyList()
        val configClient = config["client"] as? Collection<String> ?: emptyList()
        val configServer = config["server"] as? Collection<String> ?: emptyList()

        if (mixins.isNotEmpty()) {
            config["mixins"] = (configMixins + mixins).distinct().sorted()
        }
        if (client.isNotEmpty()) {
            config["client"] = (configClient + client).distinct().sorted()
        }
        if (server.isNotEmpty()) {
            config["server"] = (configServer + server).distinct().sorted()
        }

        out.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(config)))
    }
}
