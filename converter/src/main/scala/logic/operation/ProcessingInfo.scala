package dope.nathan.movement.data.converter
package logic.operation

import logic.SensorKey

import dope.nathan.movement.data.model.event.SensorDataGot
import org.joda.time.DateTime

case class ProcessingInfo(
  sensorKey: SensorKey,
  startWindow: Long,
  endWindow: Long,
  maxTimestampWindow: Long,
  timestamps: Iterable[Long],
  currentProcessingTime: Long,
  currentWatermark: Long) {
  override def toString: String = {
    def prettyTime: Long => (Long, DateTime) =
      l => (l, new DateTime(l))

    s"""ProcessingInfo{
       |sensorKey= $sensorKey,
       |startWindow= ${prettyTime(startWindow)},
       |endWindow= ${prettyTime(endWindow)},
       |maxWindowTimestamp= ${prettyTime(maxTimestampWindow)},
       |allWindowTimestamps= [\n${timestamps.map(prettyTime).mkString(",\n")}\n],
       |currentProcessingTime= ${prettyTime(currentProcessingTime)},
       |currentWatermark= ${prettyTime(currentWatermark)}
       |}""".stripMargin
  }
}

object ProcessingInfo {
  def apply(
    sensorKey: SensorKey,
    context: SensorDataGotToTrackMade.Context,
    elements: Iterable[SensorDataGot]
  ): ProcessingInfo = {
    val timestamps = elements.map(_.sensor.metrics.timestamp)

    ProcessingInfo(
      sensorKey: SensorKey,
      context.window.getStart,
      context.window.getEnd,
      context.window.maxTimestamp,
      timestamps,
      context.currentProcessingTime,
      context.currentWatermark
    )
  }
}
