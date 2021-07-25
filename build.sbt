import sbt._
import sbt.Keys._
import Dependency._
import Const._

lazy val root = (project in file("."))
  .settings(
    name := projectName,
    version := "0.1",
    publish / skip := true,
    commonSettings
  )
  .withId(projectName)
  .aggregate(
    pipeline,
    datamodel,
    tracker,
    converter
  )

lazy val pipeline = appModule(postfix)
  .enablePlugins(CloudflowApplicationPlugin)
  .settings(
    blueprint := Some("blueprint.conf"),
    runLocalConfigFile := Some(s"$projectResources/local.conf")
  )
  .dependsOn(datamodel, tracker, converter)

lazy val datamodel = appModule("datamodel")
  .enablePlugins(CloudflowLibraryPlugin)

lazy val tracker = appModule("tracker")
  .enablePlugins(CloudflowAkkaPlugin)
  .settings(
    libraryDependencies ++= commons
  )
  .dependsOn(datamodel)

lazy val converter = appModule("converter")
  .enablePlugins(CloudflowFlinkPlugin)
  .settings(
    libraryDependencies ++= commons ++ flinkTestKit,
    Test / parallelExecution := false
  )
  .dependsOn(datamodel)

def appModule(moduleID: String): Project = {
  Project(id = moduleID, base = file(moduleID))
    .settings(
      name := moduleID,
      idePackagePrefix := Some(s"$company.$namePart1.$namePart2.$moduleID"),
      commonSettings,
      Compile / resources += file(s"$projectResources/logback.xml"),
      excludeDependencies += "org.slf4j" % "slf4j-log4j12"
    )
    .withId(moduleID)
}

lazy val commonSettings = Seq(
  organization := company,
  scalaVersion := "2.12.14",
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-target:jvm-1.8",
    "-Xlog-reflective-calls",
    "-Xlint",
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-deprecation",
    "-feature",
    "-language:_",
    "-unchecked"
  ),
  Test / console / scalacOptions := (Compile / console / scalacOptions).value
)
