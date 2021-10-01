package dope.nathan.movement.data.converter
package logic.config

import cloudflow.streamlets.{DurationConfigParameter, StreamletContext}
import dope.nathan.movement.data.common.auxiliary.Logging
import org.apache.flink.api.common.time.{Time => SerializableTime}
import org.apache.flink.streaming.api.windowing.time.{Time => NonSerializableTime}

case class WindowConfig(
  trackWindowDuration: NonSerializableTime,
  maxTimeDelayOfTrackPoints: SerializableTime,
  trackWindowReleaseTimeout: NonSerializableTime) {
  override def toString: String =
    s"WindowConfig(" +
      s"trackWindowDuration= ${trackWindowDuration.toMilliseconds} ms, " +
      s"maxTimeDelayOfTrackPoints= ${maxTimeDelayOfTrackPoints.toMilliseconds} ms, " +
      s"trackWindowReleaseTimeout= ${trackWindowReleaseTimeout.toMilliseconds} ms)"
}

case object WindowConfig extends Logging {

  val trackWindowDurationParameter: DurationConfigParameter = DurationConfigParameter(
    key = "track-window-duration",
    description =
      """
        |The size (duration) of the window in minutes, within which the points of the target track are grouped.
        |See more info about Flink Time Window at https://ci.apache.org/projects/flink/flink-docs-release-1.10/dev/stream/operators/windows.html#window-assigners
        |See more info about Hocon Duration Format at https://github.com/lightbend/config/blob/master/HOCON.md#duration-format
        |""".stripMargin
  )

  val maxTimeDelayOfTrackPointsParameter: DurationConfigParameter = DurationConfigParameter(
    key = "max-time-delay-of-track-points",
    description =
      """
        |The (fixed) interval between the maximum seen timestamp seen in the track points and that of the watermark to be emitted.
        |See more info about Flink Max Out Of Orderness at https://ci.apache.org/projects/flink/flink-docs-release-1.10/dev/event_timestamp_extractors.html#assigners-allowing-a-fixed-amount-of-lateness
        |See more info about Hocon Duration Format at https://github.com/lightbend/config/blob/master/HOCON.md#duration-format
        |""".stripMargin
  )

  val trackWindowReleaseTimeoutParameter: DurationConfigParameter = DurationConfigParameter(
    key = "track-window-release-timeout",
    description =
      """
         |Timeout (allowed lateness), which determines how long the window state is retained.
         |The window of target track will release when the watermark passes the window's end point
         |and in the case where there is some timeout, then the window will fire again as each late track point arrives, up until the timeout expires.
         |See more info about Flink Allowed Lateness at https://ci.apache.org/projects/flink/flink-docs-release-1.10/dev/stream/operators/windows.html#allowed-lateness
         |See more info about Hocon Duration Format at https://github.com/lightbend/config/blob/master/HOCON.md#duration-format
         |""".stripMargin
  )

  val allParameters = Vector(
    trackWindowDurationParameter,
    maxTimeDelayOfTrackPointsParameter,
    trackWindowReleaseTimeoutParameter
  )

  def apply(implicit ctx: StreamletContext): WindowConfig = new WindowConfig(
    NonSerializableTime.milliseconds(trackWindowDurationParameter.value.toMillis),
    SerializableTime.milliseconds(maxTimeDelayOfTrackPointsParameter.value.toMillis),
    NonSerializableTime.milliseconds(trackWindowReleaseTimeoutParameter.value.toMillis)
  )

}
