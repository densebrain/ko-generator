# Kotlin Object Generator + Gradle Plugin

The problem - often you have projects with a combinations of runtime, static and dyanamic
configuration and you simply want Type-Checked access to all config and values
in a single place without the head ache of wrapping your properties etc.

## _**Kotlin-Object-Generator**_ (KOG) to the rescue 

A quick example (everything from auto-compile to capitalization is configurable - look at source and tests in lib)

You have a property file as follows

```properties
test.str=Densebrain
test.int=1234
test.double=10.5
test.bool=true

testRootValue=567

my.other.obj.value=It really works
```

Generates

*   `RootValues.kt` - all root (top level props) values go into this files 
```kotlin
import kotlin.Int

val TestRootValue: Int = 567
```

*   `My.kt` - the `My` namespace defined 
```kotlin
import kotlin.String

object My {
    object Other {
        object Obj {
            val Value: String = "It really works"
        }
    }
}
```

*   `Test.kt` - the `Test` namespace defined 
```kotlin
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String

object Test {
    val Str: String = "Densebrain"

    val Int: Int = 1234

    val Double: Double = 10.5

    val Bool: Boolean = true
}
```

## Use the lib

1. Dependency

```kotlin
repositories {
  maven(url = "https://dl.bintray.com/densebrain/oss")
}

dependencies {
  // Use a specific release - replace + with version
  implementation("org.densebrain.tools:ko-generator:+")
}
```

2.  Code

```kotlin
/** 
 * Create a configuration
 * 
 * For all options - see
 * @see org.densebrain.tools.ko.KOGeneratorConfig
 */ 
data class Config(
  
  // Output directory
  // this example will dump the files to your desktop
  override var outputDir: File = File("${System.getenv("HOME")}/Desktop","ko-files"),
  
  // All your prop files
  override var files: List<File> = listOf("props.01.properties"), 
  
  // Compile to classes and add to classpath   
  override var addToClasspath: Boolean = true,

  // Package to use for generated objects
  override var packageName: String = "TestPkg",
  
  // Package to use for generated objects
  override var capitalize: Boolean = true
) : KOGeneratorConfig

fun main() {
  // srcFiles will hold a List<File> with all generated
  // sources - and since addToClasspath == true
  // the compiled classes will be added to the 
  // thread context classloader
  val srcFiles = KOGenerator(Config()).execute()
  
}

```
## Gradle Plugin (works+tested on `5.3.2` - should work above `4.10`)

9 times out of 10 you will want the generator in your `buildSrc`, feel
free to use elsewhere - but the following is the common use
which will make all props available in your standard `build.kts`

Checkout `example` folder for full example

1. `buildSrc/settings.gradle.kts`

```kotlin
pluginManagement {
  repositories {
    gradlePluginPortal()
    jcenter()
    mavenCentral()
    
    // The following can likely be omitted as we publish
    // to the gradle plugin portal
    maven(url = "https://dl.bintray.com/densebrain/oss")
  }
}
```
2. `buildSrc/build.gradle.kts`

```kotlin
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
```

3. `build.gradle.kts`

```kotlin
import foo.Example

println("KO: ${Example.Name}")
```


