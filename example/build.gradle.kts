import foo.Example

tasks.create("showVars") {
  println("KO: ${Example.Name}")
}