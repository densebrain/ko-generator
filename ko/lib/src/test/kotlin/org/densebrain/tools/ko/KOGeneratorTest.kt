package org.densebrain.tools.ko

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

internal class KOGeneratorTest {

  data class Config(
    override var outputDir: File = run {
      val dir = File.createTempFile("kogenerator", "test")
      dir.delete()
      dir.mkdirs()
      dir
    },
    override var files: List<File> = listOf("props.01.properties")
      .map { filename ->
        val file = File.createTempFile("kogenerator", "test-${filename}")
        file.writeBytes(KOGeneratorTest::class.java.getResourceAsStream("/${filename}").readBytes())
        file
      },
    override var addToClasspath: Boolean = true,

    override var packageName: String = "TestPkg"
  ) : KOGeneratorConfig

  lateinit var config: Config

  @BeforeEach
  internal fun setUp() {
    config = Config()
  }

  @AfterEach
  internal fun tearDown() {

  }

  @Test
  fun testGenerator() {
    KOGenerator(config).execute()

    val clazz = Thread.currentThread().contextClassLoader.loadClass("TestPkg.Test")

    val fields = clazz.declaredFields
    val instanceProp = fields.find { it.name == "INSTANCE" }
      ?: error("Unable to find singleton instance on: ${clazz.name}")

    val instance = instanceProp.get(null)


    val strProp = instance.javaClass.kotlin.memberProperties.find { it.name == "Str" }
      ?: error("Unable to find Str prop")

    val intProp = instance.javaClass.kotlin.memberProperties.find { it.name == "Int" }
      ?: error("Unable to find Int prop")

    val doubleProp = instance.javaClass.kotlin.memberProperties.find { it.name == "Double" }
      ?: error("Unable to find Double prop")

    val boolProp = instance.javaClass.kotlin.memberProperties.find { it.name == "Bool" }
      ?: error("Unable to find Bool prop")



    assertAll("props",
      listOf(
        Triple(strProp, String::class, "Densebrain"),
        Triple(intProp, Int::class, 1234),
        Triple(doubleProp, Double::class, 10.5),
        Triple(boolProp, Boolean::class, true)
      ).map { (prop, klazz, expected) ->
        Executable {
          val value = prop.invoke(instance)
          assert(klazz.isInstance(value))
          assertEquals(value, expected)
        }
      }
    )
  }
}