import sbt.Keys._
import sbt._

object ProjectConst {
  lazy val ScalaVersion                    = "2.12.14"
  lazy val Company                         = "dope.nathan"
  lazy val (namePart1, namePart2, postfix) = ("movement", "data", "pipeline")
  lazy val ProjectName                     = s"$namePart1-$namePart2-$postfix"
  lazy val ProjectVersion                  = "0.1"
  lazy val ProjectResources                = s"$postfix/src/main/resources"
}

object CustomSettings {
  private lazy val scalacOps = Seq(
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
  )

  lazy val commons = Seq(
    organization := ProjectConst.Company,
    scalaVersion := ProjectConst.ScalaVersion,
    scalacOptions ++= scalacOps,
    Test / console / scalacOptions := (Compile / console / scalacOptions).value
  )
}

object Dependency {
  val FlinkVer = "1.10.0"

  val JodaTime  = "joda-time"      % "joda-time"       % "2.10.10"
  val Logback   = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val ScalaTest = "org.scalatest"  %% "scalatest"      % "3.2.0" % Test

  val commons = Seq(JodaTime, Logback, ScalaTest)

  val AkkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % "2.6.13"

  val FlinkTestUtils = "org.apache.flink" %% "flink-test-utils"     % FlinkVer % Test
  val FlinkRuntime   = "org.apache.flink" %% "flink-runtime"        % FlinkVer % Test classifier "tests"
  val FlinkStreaming = "org.apache.flink" %% "flink-streaming-java" % FlinkVer % Test classifier "tests"

  val flinkTestKit = Seq(FlinkTestUtils, FlinkRuntime, FlinkStreaming)
}
