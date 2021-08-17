package dope.nathan.movement.data.converter

import logic.state.StateConfig._

object Config {

  private val prefixConfigPath = "cloudflow.streamlets.testFlinkStreamlet"

  val configMap: Map[String, Any] = Map(
    s"$prefixConfigPath.${stateByTrackDuration.key}"     -> "minute",
    s"$prefixConfigPath.${stateReleaseTimeoutParam.key}" -> "10s",
    s"$prefixConfigPath.${stateTimeToLiveParam.key}"     -> "1m"
  )

}
