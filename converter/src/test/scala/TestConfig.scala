package dope.nathan.movement.data.converter

import logic.state.StateConfig._

object TestConfig {

  private val prefixConfigPath = "cloudflow.streamlets.testFlinkStreamlet"

  val configMap: Map[String, Any] = Map(
    s"$prefixConfigPath.${stateAccumulationPeriodParam.key}"  -> "minute",
    s"$prefixConfigPath.${releaseStateTimeoutParam.key}"   -> "10s",
    s"$prefixConfigPath.${stateTimeToLiveParam.key}"     -> "1m"
  )

}
