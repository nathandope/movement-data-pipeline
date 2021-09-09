package dope.nathan.movement.data.converter

import logic.config.EnvironmentConfig.autoWatermarkIntervalParameter
import logic.config.WindowConfig._

object TestStreamletConfig {

  private val prefixConfigPath = "cloudflow.streamlets.testFlinkStreamlet"

  val configMap: Map[String, Any] = Map(
    s"$prefixConfigPath.${autoWatermarkIntervalParameter.key}"     -> "200ms",
    s"$prefixConfigPath.${trackWindowDurationParameter.key}"       -> "1m",
    s"$prefixConfigPath.${maxTimeDelayOfTrackPointsParameter.key}" -> "5s",
    s"$prefixConfigPath.${trackWindowReleaseTimeoutParameter.key}" -> "30s"
  )

}
