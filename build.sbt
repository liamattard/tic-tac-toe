name         := "tictactoe"
version      := "1.0.0"
scalaVersion := "2.13.18"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

libraryDependencies += "org.scalameta" %% "munit" % "1.2.1" % Test

Compile / run / fork         := true
Compile / run / connectInput := true

assembly / mainClass       := Some("tictactoe.Main")
assembly / assemblyJarName := "tictactoe.jar"

lazy val root = project.in(file("."))

// Optional browser prototype; the terminal application remains the default project.
lazy val web = project
  .in(file("web"))
  .dependsOn(LocalRootProject)
  .settings(
    name         := "tictactoe-web-demo",
    scalaVersion := "2.13.18",
    libraryDependencies ++= Seq(
      "com.lihaoyi"   %% "ujson" % "4.4.3",
      "org.scalameta" %% "munit" % "1.2.1" % Test
    ),
    Compile / run / fork       := true,
    Compile / run / mainClass  := Some("tictactoe.web.WebMain"),
    assembly / mainClass       := Some("tictactoe.web.WebMain"),
    assembly / assemblyJarName := "tictactoe-web.jar"
  )
