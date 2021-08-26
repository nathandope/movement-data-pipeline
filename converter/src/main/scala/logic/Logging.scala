package dope.nathan.movement.data.converter
package logic

import logic.operation.{SensorComplexKey, SensorDataGotToTrackMade}

import org.slf4j.{Logger, LoggerFactory}

trait Logging { lazy val log: Logger = LoggerFactory.getLogger(getClass) }

trait WindowProcessLogging extends Logging {
  protected def logProcess(
    key: SensorComplexKey,
    context: SensorDataGotToTrackMade.Context,
    timestamps: Iterable[Long],
    log: => String => Unit
  ): String => Unit = stage => {
    val msg = s"""$stage process ${context.window} with key= $key ...
       |timestamps= ${timestamps.mkString(", ")}
       |currentProcessingTime= ${context.currentProcessingTime}
       |currentWatermark= ${context.currentWatermark}
       |""".stripMargin

    log(msg)
  }
}
