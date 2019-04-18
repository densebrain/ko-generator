//include(":plugin")
//include(":example")
//project(":plugin").apply {
//  name = "ko-generator-plugin"
//}

//pluginManagement {
//  repositories {
//    gradlePluginPortal()
//    jcenter()
//  }
//
//  resolutionStrategy {
//    eachPlugin {
//
//      val module = when {
//        //requested.id.name == "kdux.android.plugin" -> if (requested.)
////        requested.id.namespace == "com.android" -> "com.android.tools.build:gradle:${Versions.plugins.android}"
//        //requested.id.namespace == "com.jfrog" -> "com.jfrog.bintray.gradle:gradle-bintray-plugin:${Versions.plugins.bintray}"
////        requested.id.namespace == "org.jetbrains.kotlin.frontend" -> "org.jetbrains.kotlin:kotlin-frontend-plugin:${Versions.plugins.kotlinFrontend}"
//        //requested.id.namespace?.startsWith("org.jetbrains.kotlin") == true -> "org.jetbrains.kotlin:kotlin-gradle-plugin"
//        else -> null
//      }
//
//      logger.quiet("Plugin requested (${requested.id.namespace}/${requested.id.name}): ${module}")
//      if (module != null) {
//        useModule(module)
//      }
//
//    }
//  }
//}