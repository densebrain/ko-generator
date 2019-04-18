buildscript {
  dependencies {

  }
}
repositories {
  gradlePluginPortal()
  jcenter()
}

plugins {
  base
  java
  `kotlin-dsl`
}

object Versions {
  val bintray = "1.8.4"
}

repositories {
  jcenter()
  mavenCentral()
  gradlePluginPortal()
  google()
}

dependencies {
  "implementation"("com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.bintray}")
}