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
  id("java-library")
  `kotlin-dsl`
  `maven-publish`
  id("com.jfrog.bintray")

}

group = properties["GROUP"] as String
version = rootProject.properties["VERSION"] as String

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

configure<PublishingExtension> {
  repositories {
    val repoDir = File("${rootDir.parentFile.absolutePath}/build/repository")
    repoDir.mkdirs()

    maven(url = repoDir.absolutePath)
  }

  publications.create("${project.name}-publication",MavenPublication::class.java) {
    from(components["java"])
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
  "implementation"(Deps.kotlinReflect)
  "implementation"(Deps.kotlinCompiler)
  "implementation"(Deps.kotlinPoet)
  "testImplementation"(Deps.junitApi)
  "testRuntimeOnly"(Deps.junitEngine)
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