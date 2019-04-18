package org.densebrain.tools.ko

import java.io.File

interface KOGeneratorConfig {
  val outputDir: File
  val files: List<File>
  val addToClasspath: Boolean
  val classLoader: ClassLoader
    get() = Thread.currentThread().contextClassLoader
  val packageName: String
    get() = ""
  val capitalize: Boolean
    get() = true
}

