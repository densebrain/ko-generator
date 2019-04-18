
object Versions {
  val kotlinPoet = "1.2.0"
  val junit = "5.4.2"
  val bintray = "1.8.4"
  val kotlin = "1.3.21"
}

object Deps {
  val bintray = "com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.bintray}"

  val kotlinPoet = "com.squareup:kotlinpoet:${Versions.kotlinPoet}"
  val junitApi = "org.junit.jupiter:junit-jupiter-api:${Versions.junit}"
  val junitEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit}"

  val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect"
  val kotlinCompiler = "org.jetbrains.kotlin:kotlin-compiler-embeddable"

  val gradleKotlinPlugin = "org.gradle.kotlin:plugins:+"
  val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"

}