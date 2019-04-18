import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import java.util.Date

buildscript {
  repositories {
    jcenter()
    mavenCentral()
    google()
    gradlePluginPortal()
  }
}

plugins {
  `kotlin-dsl`
  id("com.gradle.plugin-publish") version "0.10.1"
  `java-gradle-plugin`
  id("java-library")
  id("com.jfrog.bintray")

}

group = properties["GROUP"] as String
version = rootProject.properties["VERSION"] as String

gradlePlugin {
  plugins {
    create(project.name) {
      id = "${project.group}.${project.name}"
      implementationClass = "org.densebrain.gradle.KOGeneratorPlugin"
    }
  }
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
  archiveClassifier.set("sources")
  from(kotlin.sourceSets["main"].kotlin.srcDirs)
}


/**
 * Artifacts (SOURCES)
 */
artifacts {
  add("archives", sourcesJar)
}

pluginBundle {

  website = "https://github.com/densebrain/ko-generator-plugin"
  vcsUrl = "https://github.com/densebrain/ko-generator-plugin"

  plugins {
    getByName(project.name) {
      displayName = "Kotlin Object (KO) Generator"
      description = "Properties converted to Kotlin Objects"
      tags = listOf("kotlin","properties","plugin","generate","generator","auto","buildSrc")
      version = project.version as String
    }
  }

  mavenCoordinates {
    groupId = project.group as String
    artifactId = project.name
    version = project.version as String
  }
}

configure<PublishingExtension> {
  repositories {
    val repoDir = File("${rootDir.parentFile.absolutePath}/build/repository")
    repoDir.mkdirs()

    maven(url = repoDir.absolutePath)
  }

  publications.withType(MavenPublication::class.java) {
    groupId = project.group as String
    artifactId = project.name
    version = project.version as String

    artifact(sourcesJar.get())
  }
}


repositories {
  jcenter()
  mavenCentral()
  google()
  gradlePluginPortal()
}


dependencies {
  implementation("org.densebrain.tools:ko-generator:${version}")
  implementation(Deps.kotlinReflect)
  implementation(Deps.kotlinCompiler)
  implementation(Deps.gradleKotlinPlugin)
  implementation(Deps.kotlinPoet)

  testImplementation(Deps.junitApi)
  testRuntimeOnly(Deps.junitEngine)
}


tasks.withType<Test> {
  useJUnitPlatform()
}

afterEvaluate {
  configure<BintrayExtension> {
    user = "jonglanz"
    key = System.getenv("BINTRAY_API_KEY") ?: ""
    publish = true
    override = true
    setPublications(*publishing.publications.map { it.name }.toTypedArray())
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
      repo = "oss"
      name = project.name
      userOrg = "densebrain"
      setLicenses("MIT")
      vcsUrl = "https://github.com/densebrain/ko-generator.git"
      setVersion(VersionConfig().apply {
        released = Date().toString()
        name = project.version as String
      })
    })
  }

  tasks.getByName("publish").dependsOn("bintrayUpload", "assemble")
}