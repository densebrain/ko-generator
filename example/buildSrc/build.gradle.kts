plugins {
  id("org.densebrain.gradle.ko-generator-plugin")
}


/**
 * For more options
 * @see org.densebrain.gradle.KOGeneratorExtension
 */
koGenerator {

  // Output dir - where generation files go
  outputDir = File(buildDir,"ko")

  // PKG
  packageName = "foo"

}
