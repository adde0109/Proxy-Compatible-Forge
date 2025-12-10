import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.wagyourtail.jvmdg.gradle.task.DowngradeJar
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar
import java.time.Instant

plugins {
    id("java")
    id("idea")
    id("eclipse")
    alias(libs.plugins.jvmdowngrader)
    alias(libs.plugins.shadow)
    alias(libs.plugins.spotless)
    alias(libs.plugins.unimined) apply(false)
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")
    apply(plugin = "eclipse")
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    java.toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    java.sourceCompatibility = JavaVersion.toVersion(javaVersion)
    java.targetCompatibility = JavaVersion.toVersion(javaVersion)

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.neuralnexus.dev/releases")
        maven("https://maven.neuralnexus.dev/snapshots")
        maven("https://maven.neuralnexus.dev/mirror")
        maven("https://libraries.minecraft.net/")
        maven("https://api.modrinth.com/maven")
    }

    dependencies {
        compileOnly(rootProject.libs.annotations)
    }

    spotless {
        format("misc") {
            target("*.gradle.kts", ".gitattributes", ".gitignore")
            trimTrailingWhitespace()
            leadingTabsToSpaces()
            endWithNewline()
        }
        java {
            target("src/**/*.java", "src/**/*.java.peb")
            toggleOffOn()
            importOrder()
            removeUnusedImports()
            cleanthat()
            googleJavaFormat("1.24.0")
                    .aosp()
                    .formatJavadoc(true)
                    .reorderImports(true)
            formatAnnotations()
            trimTrailingWhitespace()
            leadingTabsToSpaces()
            endWithNewline()
        }
    }

    tasks.assemble.get().dependsOn(tasks.spotlessApply)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
java.sourceCompatibility = JavaVersion.toVersion(javaVersion)
java.targetCompatibility = JavaVersion.toVersion(javaVersion)

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.neuralnexus.dev/releases")
}

//val legacy = listOf(":legacy:v12_2")

val projs = listOf(
    ":legacy:v12_2",
    ":modern:v14_4",
    ":modern:v16_5",
    ":modern:v17_1",
    ":modern:v18_2",
    ":modern:v19_2",
    ":modern:v19_4",
    ":modern:v20_2",
    ":modern:v20_4",
    ":modern:v21_10"
)

val mergeMixins = tasks.register("mergeMixins", MergeMixinConfigs::class) {
    dependsOn(":common:build")
    projs.forEach { dependsOn("$it:build") }

    val jars = mutableListOf<RegularFile>()
    jars.add(rootProject.project(":common").tasks.shadowJar.get().archiveFile.get())
    projs.forEach { jars.add(rootProject.project(it).tasks.jar.get().archiveFile.get()) }
    inputFiles.set(jars)
    outputFile.set(layout.buildDirectory.file("tmp/$modId.mixins.json"))

    config.set(mapOf(
        "compatibilityLevel" to "JAVA_8",
        "minVersion" to "0.8",
        "injectors" to mapOf("defaultRequire" to 1),
        "required" to true,
        "plugin" to "org.adde0109.pcf.mixin.plugin.PCFMixinPlugin",
        "package" to "org.adde0109.pcf.mixin"
    ))
    match.set("pcf.mixins.*")
}

val shadeAndRelocate = tasks.register<ShadowJar>("shadeAndRelocate") {
    relocate("dev.neuralnexus.taterapi", "org.adde0109.pcf.lib.taterapi")

    var mcVersion = "1.14-1.21.x"
    archiveFileName = "proxy-compatible-forge-${version}-mono.jar"
    destinationDirectory = file("./build/libs")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            mapOf(
                "Specification-Title" to "$modName $mcVersion",
                "Specification-Version" to version,
                "Specification-Vendor" to "adde0109",
                "Implementation-Version" to version,
                "Implementation-Vendor" to "adde0109",
                "Implementation-Timestamp" to Instant.now().toString(),
                "FMLCorePluginContainsFMLMod" to "true",
                "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
                "MixinConfigs" to "$modId.mixins.json"
            )
        )
    }

    from(listOf("README.md", "LICENSE")) {
        into("META-INF")
    }

    val jarTasks = mutableListOf<Task>()

    evaluationDependsOn(":common")
    dependsOn(":common:build")
    jarTasks.add(rootProject.project(":common").tasks.named<ShadowJar>("shadowJar").get())
    projs.forEach {
        evaluationDependsOn(it)
        dependsOn("$it:build")
        jarTasks.add(rootProject.project(it).tasks.jar.get())
    }

    from(bundleJars(jarTasks))
    from(mergeMixins.map { project.fileTree(it.outputFile) })
}

val customDowngrade = tasks.register<DowngradeJar>("customDowngrade") {
    inputFile.set(shadeAndRelocate.get().archiveFile)
    downgradeTo = JavaVersion.VERSION_1_8
    classpath = sourceSets.main.get().compileClasspath
    archiveClassifier = "downgraded-8"
}

val customShadeDowngradedApi = tasks.register<ShadeJar>("customShadeDowngradedApi") {
    inputFile.set(customDowngrade.get().archiveFile)
    shadePath = {
        it.substringBefore(".")
            .substringBeforeLast("-")
            .replace(Regex("[.;\\[/]"), "-")
            .replace("pcf", "org/adde0109/pcf/lib/jvmdg")
    }
    archiveFileName = "pcf-${version}.jar"
}

val renameJar = tasks.register<Copy>("renameJar") {
    from(customShadeDowngradedApi.get().archiveFile)
    into(file("./build/libs"))
    rename { "proxy-compatible-forge-${version}.jar" }

    doLast {
        file("./build/libs/proxy-compatible-forge-${version}-mono.jar").delete()
        file("./build/libs/proxy-compatible-forge-${version}-downgraded-8.jar").delete()
        file("./build/libs/pcf-${version}.jar").delete()
    }
}

tasks.assemble.get().dependsOn(renameJar)
