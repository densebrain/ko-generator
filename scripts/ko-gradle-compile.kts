//#/usr/bin/env kscript
@file:MavenRepository("ko-generator-releases","https://dl.bintray.com/densebrain/oss")
@file:DependsOn("org.densebrain.tools:ko-generator:1.0.2")
@file:DependsOn("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.3.21")
@file:DependsOn("org.jetbrains.kotlin:kotlin-reflect:1.3.21")
@file:DependsOn("com.squareup:kotlinpoet:1.2.0")
// https://mvnrepository.com/artifact/org.jetbrains.intellij.deps/trove4j
@file:DependsOn("org.jetbrains.intellij.deps:trove4j:1.0.20181211")

import java.io.File
import org.densebrain.tools.ko.KOGenerator
import org.densebrain.tools.ko.KOGeneratorConfig

val projectDir = File(args[0])
if (!projectDir.exists() || !projectDir.isDirectory) {
  error("${projectDir.absolutePath} is not a valid directory")
}

val config = object : KOGeneratorConfig {
  override var outputDir: File = File("${projectDir}/build/ko-script")
  override val files = run {
    var files = listOf<File>()
    var dir: File? = projectDir
    while (true) {
      if (dir == null)
        break

      val propFiles = listOf("gradle.properties","local.properties").map { File(dir,it) }.filter {it.exists()}
      files = listOf(*propFiles.toTypedArray(),*files.toTypedArray())
      dir = dir.parentFile
    }
    files
  }
  override var addToClasspath = true
  override var classLoader: ClassLoader = Thread.currentThread().contextClassLoader
  override var capitalize: Boolean = true
  override var packageName: String = "Script"
}

if (config.outputDir.exists()) {
  config.outputDir.deleteRecursively()
}
config.outputDir.mkdirs()

KOGenerator(config).execute()