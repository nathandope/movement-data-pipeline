package dope.nathan.movement.data.converter
package logic.state

import cloudflow.streamlets.{ DurationConfigParameter, StreamletContext, StringConfigParameter }

trait StateConfigValidation {
  protected def check(
    stateAccumulationPeriodParam: StringConfigParameter,
    stateReleaseTimeoutParam: DurationConfigParameter,
    stateTimeToLiveParam: DurationConfigParameter
  )(implicit ctx: StreamletContext
  ): Either[String, (StatePeriod, Long, Long)] = {
    statePeriodStringToInstance(stateAccumulationPeriodParam).flatMap { stateAccumulationPeriod =>
      val releaseStateTimeoutMs     = stateReleaseTimeoutParam.value.toMillis
      val stateTimeToLiveMs         = stateTimeToLiveParam.value.toMillis
      val stateAccumulationPeriodMs = stateAccumulationPeriod.value.toMillis

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
