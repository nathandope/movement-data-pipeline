import sbt.Keys._
import sbt._

object Const {
  lazy val ScalaVersion                    = "2.12.14"
  lazy val Company                         = "dope.nathan"
  lazy val (namePart1, namePart2, postfix) = ("movement", "data", "pipeline")
  lazy val ProjectName                     = s"$namePart1-$namePart2-$postfix"
  lazy val ProjectVersion                  = "0.1"
  lazy val ProjectResources                = s"$ProjectName/src/main/resources"
}

object Settings {
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
    organization := Const.Company,
    scalaVersion := Const.ScalaVersion,
    scalacOptions ++= scalacOps,
    Test / console / scalacOptions := (Compile / console / scalacOptions).value
  )
}

object Dependency {
  val JodaTime  = "joda-time"      % "joda-time"       % "2.10.10"
  val Logback   = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val ScalaTest = "org.scalatest"  %% "scalatest"      % "3.2.0" % Test

  val commons = Seq(JodaTime, Logback, ScalaTest)

  val FlinkTestUtils = "org.apache.flink" %% "flink-test-utils"     % "1.10.0" % Test
  val FlinkRuntime   = "org.apache.flink" %% "flink-runtime"        % "1.10.0" % Test classifier "tests"
  val FlinkStreaming = "org.apache.flink" %% "flink-streaming-java" % "1.10.0" % Test classifier "tests"

  val flinkTestKit = Seq(FlinkTestUtils, FlinkRuntime, FlinkStreaming)

  val AkkaTestKit = "com.typesafe.akka" %% "akka-http-testkit" % "10.2.4" % Test
}
