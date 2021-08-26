package dope.nathan.movement.data.converter
package logic.config

import logic.Logging

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

case class FlinkConfiguration(env: StreamExecutionEnvironment) extends Logging {
  def tune(
    autoWatermarkInterval: Option[Long],
    timeCharacteristic: TimeCharacteristic = TimeCharacteristic.EventTime
  ): StreamExecutionEnvironment = {
    log.info(s"Start tuning flink stream execution environment...")

    env.setStreamTimeCharacteristic(timeCharacteristic)
    autoWatermarkInterval.map(env.getConfig.setAutoWatermarkInterval)
    log.info(
      s"""End tuning flink stream execution environment:
         |${env.getConfig.toString}
         |""".stripMargin
    )

    env
  }
}
