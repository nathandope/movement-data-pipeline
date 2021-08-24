package dope.nathan.movement.data.converter
package logic.state

import logic.state.track.TrackDuration

import cloudflow.streamlets.{ DurationConfigParameter, StreamletContext, StringConfigParameter }

case class StateConfig(trackDurationForState: TrackDuration, stateReleaseTimeout: Long, stateTimeToLive: Long)

object StateConfig extends StateConfigValidation {

  val stateByTrackDuration: StringConfigParameter = StringConfigParameter(
    key = "state-by-track-duration",
    description =
      """The time interval within which the track points will be consolidated with a unique key to form a state.
        |The acceptable values: 'minute' | 'hour' | 'day' ; must be equals or less than 'state-time-to-live'.""".stripMargin
  )

  val stateReleaseTimeoutParam: DurationConfigParameter = DurationConfigParameter(
    key = "state-release-timeout",
    description =
      """The period of time after the last SensorDataGot event that occurred,
         |during which the state will not be released while waiting for new events.
         |See more detailed technical information in the Flink Documentation in the KeyedProcessFunction#Timers section.
         |The acceptable values: Must be less than 'state-time-to-live' but more than '0'.""".stripMargin
  )

  val stateTimeToLiveParam: DurationConfigParameter = DurationConfigParameter(
    key = "state-time-to-live",
    description = """The period of time during which the state will not be cleared, even if there are no calls to it.
       |See more detailed technical information in the Flink Documentation in the State TTL section.
       |The acceptable values: Must be equals or more than 'state-by-track-duration'.""".stripMargin
  )

  val allParameters = Vector(stateByTrackDuration, stateReleaseTimeoutParam, stateTimeToLiveParam)

  def apply(implicit ctx: StreamletContext): StateConfig = {
    check(stateByTrackDuration, stateReleaseTimeoutParam, stateTimeToLiveParam).fold(
      errorMsgPart => {
        val errorMsg = s"Something went wrong when reading the parameters:\n$errorMsgPart"
        throw new IllegalArgumentException(errorMsg)
      },
      params => StateConfig(params._1, params._2, params._3)
    )
  }

}
