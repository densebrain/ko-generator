
allprojects {
  configurations.all {
    resolutionStrategy.dependencySubstitution {
      substitute(module("org.densebrain.tools:ko-generator")).apply {
        with(project(":ko-generator"))
      }
    }
  }
}

