package dope.nathan.movement.data.converter
package logic.config

import cloudflow.streamlets.{ConfigParameter, StreamletContext}
import dope.nathan.movement.data.common.auxiliary.Logging

case class FlinkConfig(environmentConfig: EnvironmentConfig, windowConfig: WindowConfig) {
  override def toString: String =
    s"""FlinkConfig{
       |environmentConfig= ${environmentConfig.toString},
       |windowConfig= ${windowConfig.toString}
       |}""".stripMargin
}

case object FlinkConfig extends Logging {
  def apply(implicit ctx: StreamletContext): FlinkConfig = {
    logger.info(s"Start initialization config...")

    val environmentConfig = EnvironmentConfig.apply
    val windowConfig      = WindowConfig.apply
    val flinkConfig       = FlinkConfig(environmentConfig, windowConfig)

    logger.info(s"End initialization config: $flinkConfig")

    flinkConfig
  }

  val allParameters: Vector[ConfigParameter] =
    EnvironmentConfig.allParameters ++ WindowConfig.allParameters
}
