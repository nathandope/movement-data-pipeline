import sbt._

object Const {
  lazy val company                         = "dope.nathan"
  lazy val (namePart1, namePart2, postfix) = ("movement", "data", "pipeline")
  lazy val projectName                     = s"$namePart1-$namePart2-$postfix"
  lazy val projectResources                = s"$projectName/src/main/resources"
}

object Dependency {
  val JodaTime       = "joda-time"        % "joda-time"             % "2.10.10"
  val Logback        = "ch.qos.logback"   % "logback-classic"       % "1.2.3"
  val Scalatest      = "org.scalatest"    %% "scalatest"            % "3.0.8" % Test
  val FlinkTestUtils = "org.apache.flink" %% "flink-test-utils"     % "1.10.0" % Test
  val FlinkRuntime   = "org.apache.flink" %% "flink-runtime"        % "1.10.0" % Test classifier "tests"
  val FlinkStreaming = "org.apache.flink" %% "flink-streaming-java" % "1.10.0" % Test classifier "tests"

  val commons      = Seq(JodaTime, Logback, Scalatest)
  val flinkTestKit = Seq(FlinkTestUtils, FlinkRuntime, FlinkStreaming)
}
