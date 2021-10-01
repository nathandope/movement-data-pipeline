package dope.nathan.movement.data.converter
package logic.config

import dope.nathan.movement.data.common.auxiliary.Logging
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

case class FlinkSetup(env: StreamExecutionEnvironment) extends Logging {

  def tune(config: EnvironmentConfig): StreamExecutionEnvironment = {
    logger.info(s"Start tuning flink stream execution environment...")

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setAutoWatermarkInterval(config.autoWatermarkInterval)

    logger.info(
      s"""End tuning flink stream execution environment:
         |${env.getConfig.toString.split(", ").mkString("", ",\n", "\n")}
         |""".stripMargin
    )

    env
  }
}
