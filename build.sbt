name         := "tictactoe"
version      := "1.0.0"
scalaVersion := "2.13.18"

libraryDependencies += "org.scalameta" %% "munit" % "1.2.1" % Test

Compile / run / fork := true
Compile / run / connectInput := true
