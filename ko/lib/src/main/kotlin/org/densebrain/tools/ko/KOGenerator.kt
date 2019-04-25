package org.densebrain.tools.ko

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File
import java.net.URLClassLoader
import java.util.*


open class KOGenerator(private val config: KOGeneratorConfig) {

  private val log = KOLogger()

  private val packageName = config.packageName

  /**
   * All properties files
   */
  private val files = config.files

  /**
   * Output dir
   */
  private val outputDir = run {
    val dir = config.outputDir
    //log("Using output dir: ${dir}")
    if (dir.exists())
      dir.deleteRecursively()


    val dirsToCreate = mutableListOf<File>()
    var parent = dir.parentFile
    while (true) {
      if (parent == null || parent.exists()) {
        break
      }
      dirsToCreate.add(parent)
      parent = parent.parentFile
    }

    dirsToCreate.reversed().forEach { parentDir ->
      //log("Creating ${parentDir.absolutePath}")
      parentDir.mkdirs()
    }

    dir.mkdirs()

    require(dir.isDirectory) { "${dir} is not a valid directory" }
    dir
  }

  /**
   * Load properties from all files
   */
  private val props = run {
    val allProps = mutableMapOf<String, String?>()
    files.forEach { file ->
      //log("Loading: ${file.absolutePath}")
      val props = Properties()
      props.load(file.inputStream())

      props.forEach { (key, value) ->
        require(key is String && value is String?) { "props can only be strings" }
        allProps[key] = value
      }
    }

    allProps
  }

  private fun prepareName(name: String) = if (config.capitalize) name.capitalize() else name

  /**
   * Object model container
   */
  private val root = props.toObjectContainer()

  private val outputFiles = mutableListOf<FileSpec>()

  private fun getValues(container: KOObjectContainer): List<PropertySpec> =
    container.values.map { (name, value) ->
      PropertySpec.builder(prepareName(name), value.javaClass.kotlin)
        .initializer(if (value is String) "%S" else "%L", value)
        .build()
    }

  private fun getObject(name:String, container: KOObjectContainer): TypeSpec {
    val builder = TypeSpec.Companion.objectBuilder(prepareName(name))
    container.values.forEach { (name, value) ->
      builder.addProperty(PropertySpec.builder(prepareName(name), value.javaClass.kotlin)
        .initializer(if (value is String) "%S" else "%L", value)
        .build())
    }
    container.containers.forEach { (name, value) ->
      builder.addType(getObject(prepareName(name),value))
    }
    return builder.build()
  }

  fun execute():List<File> {
    val rootValues = getValues(root)
    if (rootValues.isNotEmpty()) {
      val builder = FileSpec.builder(packageName, "RootValues")
      rootValues.forEach(builder::addProperty)
      outputFiles.add(builder.build())
    }

    outputFiles.addAll(root.containers.map { (name, container) ->
      val builder = FileSpec.builder(packageName,prepareName(name))
      builder.addType(getObject(prepareName(name), container))
      builder.build()
    })

    val srcFiles = outputFiles.map { fileSpec ->
      log("Writing ${fileSpec.packageName}.${fileSpec.name} to ${outputDir.absolutePath}")
      fileSpec.writeTo(outputDir)

      File(
        "${outputDir.absolutePath}${File.separator}${packageName.replace(".", File.separator)}",
        "${fileSpec.name}.kt"
      )
    }

    if (config.addToClasspath) {
      val compilerConfig = CompilerConfiguration().apply {
        put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, kotlinReporter)
        put(CommonConfigurationKeys.LANGUAGE_VERSION_SETTINGS, LanguageVersionSettingsImpl(
          languageVersion = LanguageVersion.KOTLIN_1_3,
          apiVersion = ApiVersion.KOTLIN_1_3,
          specificFeatures = mapOf(
            LanguageFeature.NewInference to LanguageFeature.State.ENABLED,
            LanguageFeature.SamConversionForKotlinFunctions to LanguageFeature.State.ENABLED
          ),
          analysisFlags = mapOf(
            AnalysisFlags.skipMetadataVersionCheck to true
          )
        ))
        addKotlinSourceRoots(listOf(outputDir.canonicalPath))
        put(JVMConfigurationKeys.OUTPUT_DIRECTORY, outputDir)
        put(CommonConfigurationKeys.MODULE_NAME, "ko-gen")
        addJvmClasspathRoot(PathUtil.getResourcePathForClass(Unit::class.java))
      }

      if (!withRootDisposable {
          val environment = KotlinCoreEnvironment.createForProduction(this, compilerConfig, EnvironmentConfigFiles.JVM_CONFIG_FILES)
          KotlinToJVMBytecodeCompiler.compileBunchOfSources(environment)
        }) kotlin.error("Compilation failed")

      Thread.currentThread().contextClassLoader = URLClassLoader(arrayOf(outputDir.toURI().toURL()),Thread.currentThread().contextClassLoader)

    }
    return srcFiles
  }

  private val kotlinReporter = object : MessageCollector {

    override fun clear() {

    }

    private var hasErrors_ = false

    override fun hasErrors(): Boolean {
      return hasErrors_
    }

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
      if (arrayOf(CompilerMessageSeverity.ERROR,CompilerMessageSeverity.EXCEPTION).contains(severity))
        hasErrors_ = true

      log("${severity.name}: ${message}\n${location}")
    }
  }

  private inline fun <T> withRootDisposable(action: Disposable.() -> T): T {
    val rootDisposable = Disposer.newDisposable()
    try {
      return action(rootDisposable)
    } finally {
      Disposer.dispose(rootDisposable)
    }
  }

}

