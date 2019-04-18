

tasks {

    val plugin by registering(GradleBuild::class) {
        dir = file("ko")
        tasks = listOf("publishAllPublicationsToMaven2Repository")
    }

    val example by registering(GradleBuild::class) {
        dir = file("example")
        tasks = listOf("showVars")
    }

    example {
        dependsOn(plugin)
    }
}
