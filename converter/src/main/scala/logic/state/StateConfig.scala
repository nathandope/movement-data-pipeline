package dope.nathan.movement.data.converter
package logic.state

import cloudflow.streamlets.{ DurationConfigParameter, StreamletContext, StringConfigParameter }

case class StateConfig(accumulationPeriod: StatePeriod, releaseTimeout: Long, stateTimeToLive: Long)

object StateConfig {

  private val stateAccumulationPeriodParam = StringConfigParameter(
    key = "state-accumulation-period",
    description = "The time interval within which the track points will be consolidated with a unique key to form a state. " +
      "The acceptable values: 'minute' | 'hour' | 'day' ; must be equals or less than 'state-time-to-live'."
  )

  private val releaseStateTimeoutParam = DurationConfigParameter(
    key = "release-state-timeout",
    description = "The period of time after the last SensorDataGot event that occurred, " +
      "during which the state will not be released while waiting for new events. " +
      "See more detailed technical information in the Flink Documentation in the KeyedProcessFunction#Timers section. " +
      "The acceptable values: Must be less than 'state-time-to-live' but more than '0'."
  )

  private val stateTimeToLiveParam = DurationConfigParameter(
    key = "state-time-to-live",
    description = "The period of time during which the state will not be cleared, even if there are no calls to it. " +
      "See more detailed technical information in the Flink Documentation in the State TTL section. " +
      "The acceptable values: Must be equals or more than 'state-accumulation-period'."
  )

  val allParameters = Vector(stateAccumulationPeriodParam, releaseStateTimeoutParam, stateTimeToLiveParam)

  def apply(implicit ctx: StreamletContext): StateConfig = {
    check(stateAccumulationPeriodParam, releaseStateTimeoutParam, stateTimeToLiveParam).fold(
      errorMsgPart => {
        val errorMsg = s"Something went wrong when reading the parameters:\n$errorMsgPart"
        throw new IllegalArgumentException(errorMsg)
      },
      params => StateConfig(params._1, params._2, params._3)
    )
  }

  private def check(
    stateAccumulationPeriodParam: StringConfigParameter,
    stateReleaseTimeoutParam: DurationConfigParameter,
    stateTimeToLiveParam: DurationConfigParameter
  )(implicit ctx: StreamletContext
  ): Either[String, (StatePeriod, Long, Long)] = {
    statePeriodStringToInstance(stateAccumulationPeriodParam).flatMap { stateAccumulationPeriod =>
      val releaseStateTimeoutMs     = stateReleaseTimeoutParam.value.toMillis
      val stateTimeToLiveMs         = stateTimeToLiveParam.value.toMillis
      val stateAccumulationPeriodMs = stateAccumulationPeriod.value.toMillis

      if (0 <= releaseStateTimeoutMs) {
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

      } else if (stateTimeToLiveMs < stateAccumulationPeriodMs) {
        val errorMsgPart =
          s"""${stateTimeToLiveParam.key}= $stateTimeToLiveMs ms.
           |${stateTimeToLiveParam.description}
           |or
           |${stateAccumulationPeriodParam.key}= $stateAccumulationPeriodMs ms.
           |${stateAccumulationPeriodParam.description}
           |""".stripMargin

        Left(errorMsgPart)

      } else {
        Right((stateAccumulationPeriod, releaseStateTimeoutMs, stateTimeToLiveMs))
      }
    }
  }

  private def statePeriodStringToInstance(
    stateAccumulationPeriodParam: StringConfigParameter
  )(implicit ctx: StreamletContext
  ): Either[String, StatePeriod] = {
    stateAccumulationPeriodParam.value match {
      case "minute" => Right(StateMinute)
      case "hour"   => Right(StateHour)
      case "day"    => Right(StateDay)
      case period =>
        val errorMsgPart =
          s"""${stateAccumulationPeriodParam.key}= $period.
             |${stateAccumulationPeriodParam.description}
             |""".stripMargin

        Left(errorMsgPart)
    }
  }
}
