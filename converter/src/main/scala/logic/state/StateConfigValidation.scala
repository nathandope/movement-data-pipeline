package dope.nathan.movement.data.converter
package logic.state

import logic.state.track.{ Daily, Hourly, Minutely, TrackDuration }

import cloudflow.streamlets.{ DurationConfigParameter, StreamletContext, StringConfigParameter }

trait StateConfigValidation {
  protected def check(
    stateByTrackDurationParam: StringConfigParameter,
    stateReleaseTimeoutParam: DurationConfigParameter,
    stateTimeToLiveParam: DurationConfigParameter
  )(implicit ctx: StreamletContext
  ): Either[String, (TrackDuration, Long, Long)] = {
    statePeriodStringToInstance(stateByTrackDurationParam).flatMap { stateByTrackDuration =>
      val releaseStateTimeoutMs   = stateReleaseTimeoutParam.value.toMillis
      val stateTimeToLiveMs       = stateTimeToLiveParam.value.toMillis
      val trackDurationForStateMs = stateByTrackDuration.value.toMillis

      if (0 >= releaseStateTimeoutMs) {
        val errorMsgPart =
          s"""${stateReleaseTimeoutParam.key}= $releaseStateTimeoutMs ms.
             |${stateReleaseTimeoutParam.description}
             |""".stripMargin

        Left(errorMsgPart)

      } else if (stateTimeToLiveMs < releaseStateTimeoutMs) {
        val errorMsgPart =
          s"""${stateReleaseTimeoutParam.key}= $releaseStateTimeoutMs ms.
             |${stateReleaseTimeoutParam.description}
             |or
             |${stateTimeToLiveParam.key}= $stateTimeToLiveMs ms.
             |${stateTimeToLiveParam.description}
             |""".stripMargin

        Left(errorMsgPart)

      } else if (stateTimeToLiveMs < trackDurationForStateMs) {
        val errorMsgPart =
          s"""${stateTimeToLiveParam.key}= $stateTimeToLiveMs ms.
             |${stateTimeToLiveParam.description}
             |or
             |${stateByTrackDurationParam.key}= $trackDurationForStateMs ms.
             |${stateByTrackDurationParam.description}
             |""".stripMargin

        Left(errorMsgPart)

      } else {
        Right((stateByTrackDuration, releaseStateTimeoutMs, stateTimeToLiveMs))
      }
    }
  }

  private def statePeriodStringToInstance(
    stateByTrackDurationParam: StringConfigParameter
  )(implicit ctx: StreamletContext
  ): Either[String, TrackDuration] = {
    stateByTrackDurationParam.value match {
      case "minute" => Right(Minutely)
      case "hour"   => Right(Hourly)
      case "day"    => Right(Daily)
      case period =>
        val errorMsgPart =
          s"""${stateByTrackDurationParam.key}= $period.
             |${stateByTrackDurationParam.description}
             |""".stripMargin

        Left(errorMsgPart)
    }
  }
}
