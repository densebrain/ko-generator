include(":lib")
include(":plugin")

project(":plugin").name = "ko-generator-plugin"
project(":lib").name = "ko-generator"

pluginManagement {
  repositories {
    gradlePluginPortal()
    jcenter()
    mavenCentral()
  }

  resolutionStrategy {
    eachPlugin {

      val module = when {
        requested.id.namespace == "com.jfrog" -> Deps.bintray
        requested.id.namespace?.startsWith("org.jetbrains.kotlin") == true -> Deps.kotlinGradle
        else -> null
      }

      //logger.quiet("Plugin requested (${requested.id.namespace}/${requested.id.name}): ${module}")
      if (module != null) {
        useModule(module)
      }

    }
  }
}