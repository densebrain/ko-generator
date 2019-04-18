import java.util.*

pluginManagement {

  // SECTION - Only needed for local dev of this plugin
  val koDir = rootDir.parentFile.parentFile
  val version = run {
    val props = Properties()
    props.load(File("${koDir}/ko/gradle.properties").inputStream())
    props["VERSION"]
  }
  // ENDSECTION



  repositories {
    jcenter()
    mavenCentral()
    gradlePluginPortal()

    // SECTION - Only needed for local dev of this plugin
    // The following line can normally be omitted - only required for plugin dev
    maven(url = File("${koDir}/build/repository").absolutePath)
    // ENDSECTION
  }

  resolutionStrategy {
    eachPlugin {
      if (requested.id.name == "ko-generator-plugin") {
        // In a normal use case, you'll likely replace ${version} with either
        // a dynamic version "+" or a static version "1.0.0"
        useModule("org.densebrain.gradle:ko-generator-plugin:${version}")
      }
    }
  }
}
