name := "scala-play"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.0.5" % Test)

libraryDependencies ++= Seq("com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.8",
                            "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.8")
