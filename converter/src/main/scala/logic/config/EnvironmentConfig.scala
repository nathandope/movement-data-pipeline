package dope.nathan.movement.data.converter
package logic.config

import cloudflow.streamlets.{DurationConfigParameter, StreamletContext}

case class EnvironmentConfig(autoWatermarkInterval: Long) {
  override def toString: String =
    s"EnvironmentConfig{ autoWatermarkInterval= $autoWatermarkInterval ms }"
}

object EnvironmentConfig {

  val autoWatermarkIntervalParameter: DurationConfigParameter = DurationConfigParameter(
    key = "auto-watermark-interval",
    description =
      """
        |The periodic assignment of watermarks means that we instruct the system to issue watermarks and increase the event time with fixed machine time intervals.
        |See more info about Flink Auto Watermark Interval at https://ci.apache.org/projects/flink/flink-docs-release-1.10/dev/event_timestamps_watermarks.html#with-periodic-watermarks
        |See more info about Hocon Duration Format at https://github.com/lightbend/config/blob/master/HOCON.md#duration-format
        |""".stripMargin,
    defaultValue = Some("200 ms")
  )

  val allParameters = Vector(autoWatermarkIntervalParameter)

  def apply(implicit ctx: StreamletContext): EnvironmentConfig = new EnvironmentConfig(
    autoWatermarkIntervalParameter.value.toMillis
  )
}
