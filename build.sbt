val scala3Version = "3.6.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "n-body-gravity-sim",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,

    // scalafx
    libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32",

    // adding java runtime options
    javaOptions ++= Seq(
      "--module-path", "D:/MUIC/openjfx-21.0.6_windows-x64_bin-sdk/javafx-sdk-21.0.6/lib",
      "--add-modules", "javafx.controls,javafx.fxml"
    )
  )
