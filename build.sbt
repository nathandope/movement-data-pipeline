import Dependency._
import ProjectConst._
import sbt.Keys._
import sbt._

lazy val root = (project in file("."))
  .settings(
    name := ProjectName,
    version := ProjectVersion,
    publish / skip := true,
    CustomSettings.commons
  )
  .withId(ProjectName)
  .aggregate(
    pipeline,
    datamodel,
    transceiver,
    converter,
    collector
  )

lazy val pipeline = appModule(postfix)
  .enablePlugins(CloudflowApplicationPlugin)
  .settings(
    blueprint := Some("blueprint.conf"),
    runLocalConfigFile := Some(s"$ProjectResources/sandbox/local.conf"),
    runLocalLog4jConfigFile := Some(s"$ProjectResources/sandbox/log4j.properties")
  )
  .dependsOn(datamodel, transceiver, converter, collector)

lazy val common = appModule("common")
  .settings(
    libraryDependencies += Logback,
    Test / parallelExecution := false
  )

lazy val datamodel = appModule("datamodel")
  .enablePlugins(CloudflowLibraryPlugin)

lazy val transceiver = appModule("transceiver")
  .enablePlugins(CloudflowAkkaPlugin)
  .settings(
    libraryDependencies ++= commons,
    Test / parallelExecution := false
  )
  .dependsOn(datamodel)

lazy val converter = appModule("converter")
  .enablePlugins(CloudflowFlinkPlugin)
  .settings(
    libraryDependencies ++= commons ++ flinkTestKit,
    Test / parallelExecution := false
  )
  .dependsOn(datamodel, common)

lazy val collector = appModule("collector")
  .enablePlugins(CloudflowSparkPlugin)
  .settings(
    libraryDependencies ++= (commons :+ AkkaSlf4j),
    Test / parallelExecution := false
  )
  .dependsOn(datamodel, common)

def appModule(moduleID: String): Project = {
  Project(id = moduleID, base = file(moduleID))
    .settings(
      name := moduleID,
      idePackagePrefix := Some(s"$Company.$namePart1.$namePart2.$moduleID"),
      CustomSettings.commons
    )
    .withId(moduleID)
}
