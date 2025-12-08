import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.bundling.Jar
import groovy.json.JsonSlurper;
import groovy.json.JsonOutput;
import java.io.File

object PCF

val Project.author: String get() = properties["author"].toString()
val Project.modName: String get() = properties["mod_name"].toString()
val Project.modId: String get() = properties["mod_id"].toString()
val Project.description: String get() = properties["description"].toString()
val Project.license: String get() = properties["license"].toString()

val Project.homepageUrl: String get() = properties["homepage_url"].toString()
val Project.issueUrl: String get() = properties["issue_url"].toString()
val Project.sourceUrl: String get() = properties["source_url"].toString()

val Project.minecraftVersion: String get() = properties["minecraft_version"].toString()
val Project.parchmentMinecraft: String get() = properties["parchment_minecraft"].toString()
val Project.parchmentVersion: String get() = properties["parchment_version"].toString()

val Project.mcpChannel: String get() = properties["mcp_channel"].toString()
val Project.mcpVersion: String get() = properties["mcp_version"].toString()

val Project.forgeVersion: String get() = properties["forge_version"].toString()
val Project.neoforgeVersion: String get() = properties["neoforge_version"].toString()

val Project.javaVersion: String get() = properties["java_version"].toString()

fun Project.jarToFiles(taskName: String): FileCollection {
    val jar: Jar = when (val task = tasks.getByName(taskName)) {
        is Jar -> task
        else -> throw IllegalArgumentException("Task $taskName is not a Jar task")
    }
    return zipTree(jar.archiveFile.get().asFile)
}

fun Project.jarToFiles(projectName: String, taskName: String): FileCollection {
    val jar: Jar = when (val task = rootProject.project(projectName).tasks.getByName(taskName)) {
        is Jar -> task
        else -> throw IllegalArgumentException("Task $taskName is not a Jar task")
    }
    return zipTree(jar.archiveFile.get().asFile)
}

val Project.srcSetAsDep: (String, String) -> FileCollection get() = { projName, srcSetName ->
    files(rootProject.project(projName).extensions.getByType(JavaPluginExtension::class.java).sourceSets.getByName(srcSetName).output)
}

fun Project.bundleJars(jarTasks: List<Task>): List<Provider<FileTree>> {
    return jarTasks.map { task ->
        val jar = (task as Jar)
        provider {
            zipTree(jar.archiveFile)
                .matching { exclude("pcf.mixins.*") }
        }
    }
}
