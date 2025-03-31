val scala3Version = "3.6.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "n-body-gravity-sim",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    Compile / mainClass := Some("barneshut.BarnesHutSim"),

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,

    // scalafx
    libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32",

    libraryDependencies ++= Seq(
      "org.openjfx" % "javafx-controls" % "21" classifier "win",
      "org.openjfx" % "javafx-fxml" % "21" classifier "win",
      "org.openjfx" % "javafx-base" % "21" classifier "win",
      "org.openjfx" % "javafx-graphics" % "21" classifier "win"
    ),

    // parallel collection
    libraryDependencies +=
    "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",

    // adding java runtime options
    javaOptions ++= Seq(
      "--module-path", "${env:JAVAFX_HOME}/lib",
      "--add-modules", "javafx.controls,javafx.fxml"
    )
  )
