package dope.nathan.movement.data.converter
package logic

import cloudflow.streamlets.{ DurationConfigParameter, StreamletContext }
import org.apache.flink.streaming.api.windowing.time.Time

case class WindowConfig(trackWindowDuration: Time, maxTimeDelayOfTrackPoints: Time, trackWindowReleaseTimeout: Time)

object WindowConfig {

  val trackWindowDurationParameter: DurationConfigParameter = DurationConfigParameter(
    key = "track-window-duration",
    description =
      """The size (duration) of the window in minutes, within which the points of the target track are grouped.
        |See more info about value format at https://github.com/lightbend/config/blob/master/HOCON.md#duration-format""".stripMargin
  )

  val maxTimeDelayOfTrackPointsParameter: DurationConfigParameter = DurationConfigParameter(
    key = "max-time-delay-of-track-points",
    description =
      """The (fixed) interval between the maximum seen timestamp seen in the track points and that of the watermark to be emitted.
        |See more info about value format at https://github.com/lightbend/config/blob/master/HOCON.md#duration-format""".stripMargin
  )

  val trackWindowReleaseTimeoutParameter: DurationConfigParameter = DurationConfigParameter(
    key = "track-window-release-timeout",
    description = //todo
      """The period of time after the last SensorDataGot event that occurred, 
         |See more info about value format at https://github.com/lightbend/config/blob/master/HOCON.md#duration-format""".stripMargin
  )

  val allParameters = Vector(
    trackWindowDurationParameter,
    maxTimeDelayOfTrackPointsParameter,
    trackWindowReleaseTimeoutParameter
  )

  def apply(implicit ctx: StreamletContext) = new WindowConfig(
    Time.milliseconds(trackWindowDurationParameter.value.toMillis),
    Time.milliseconds(maxTimeDelayOfTrackPointsParameter.value.toMillis),
    Time.milliseconds(trackWindowReleaseTimeoutParameter.value.toMillis)
  )

}
