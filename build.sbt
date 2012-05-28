
organization := "com.google.closure-stylesheets"

name := "closure-stylesheets"

version := "0.0.2-SNAPSHOT"

javaSource in Compile <<= sourceDirectory(x => x)

javaSource in Test <<= sourceDirectory(x => x)

unmanagedSourceDirectories in Compile <<= Seq(javaSource in Compile, baseDirectory(_ / "build" / "genfiles" / "java")).join

crossPaths := false

libraryDependencies ++= Seq(
  "args4j" % "args4j" % "2.0.16",
  "com.google.guava" % "guava" % "12.0",
  "com.google.code.gson" % "gson" % "1.7.1",
  "com.google.code.findbugs" % "jsr305" % "1.3.9"
)

