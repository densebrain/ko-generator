package org.densebrain.gradle

import org.densebrain.tools.ko.KOGenerator
import org.densebrain.tools.ko.KOGeneratorConfig
import org.densebrain.tools.ko.KOLogger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.io.File


open class KOGeneratorExtension(project:Project, override var outputDir: File) : KOGeneratorConfig {
  override val files = run {
    var files = listOf<File>()
    var dir:File? = project.projectDir
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
  override var packageName: String = ""
}



open class KOGeneratorPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.run {
      val extension = extensions.create<KOGeneratorExtension>("koGenerator",project,File(project.buildDir,"ko"))


      afterEvaluate {
//        tasks.findByName("build")?.apply {
//          dependsOn(generateTask)
//        }

        try {
          configure<KotlinProjectExtension>() {
            sourceSets["main"].kotlin.srcDir(extension.outputDir)
          }

          KOLogger.provider =  { msg: String, cause: Throwable? ->
            logger.quiet(msg, cause)
          }

          KOGenerator(extension).execute()
        } catch (cause:Throwable) {
          logger.error("This project is not a kotlin project, can not add sources", cause)
        }

//        tasks.withType<Jar> {
//          from(extension.outputDir)
//        }
      }
    }
  }




}
