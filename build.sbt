import Dependency._
import sbt.Keys._
import sbt._
import Const._

lazy val root = (project in file("."))
  .settings(
    name := ProjectName,
    version := ProjectVersion,
    publish / skip := true,
    Settings.commons
  )
  .withId(ProjectName)
  .aggregate(
    pipeline,
    datamodel,
    transceiver,
    converter
  )

lazy val pipeline = appModule(postfix)
  .enablePlugins(CloudflowApplicationPlugin)
  .settings(
    blueprint := Some("blueprint.conf"),
    runLocalConfigFile := Some(s"$ProjectResources/local.conf")
  )
  .dependsOn(datamodel, transceiver, converter)

lazy val datamodel = appModule("datamodel")
  .enablePlugins(CloudflowLibraryPlugin)

lazy val transceiver = appModule("transceiver")
  .enablePlugins(CloudflowAkkaPlugin)
  .settings(
    libraryDependencies ++= commons :+ AkkaTestKit ,
    Test / parallelExecution := false
  )
  .dependsOn(datamodel)

lazy val converter = appModule("converter")
  .enablePlugins(CloudflowFlinkPlugin)
  .settings(
    libraryDependencies ++= commons ++ flinkTestKit,
    Test / parallelExecution := false
  )
  .dependsOn(datamodel)

lazy val collector = appModule("collector")
  .enablePlugins(CloudflowSparkPlugin)
  .settings(
    libraryDependencies ++= commons,
    Test / parallelExecution := false
  )
  .dependsOn(datamodel)

def appModule(moduleID: String): Project = {
  Project(id = moduleID, base = file(moduleID))
    .settings(
      name := moduleID,
      idePackagePrefix := Some(s"$Company.$namePart1.$namePart2.$moduleID"),
      Settings.commons,
      Compile / resources += file(s"$ProjectResources/logback.xml"),
      excludeDependencies += "org.slf4j" % "slf4j-log4j12"
    )
    .withId(moduleID)
}
