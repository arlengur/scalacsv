lazy val root = project
  .in(file("."))
  .settings(
    name := "ParseCsv",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "3.2.2"
  )
  .settings(dependencies)

lazy val dependencies = Seq(
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.2.15",
    "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0"
  ).map(_ % Test)