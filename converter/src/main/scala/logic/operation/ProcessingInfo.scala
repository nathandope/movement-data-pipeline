package dope.nathan.movement.data.converter
package logic.operation

import org.joda.time.DateTime

case class ProcessingInfo(
  sensorComplexKey: SensorComplexKey,
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
       |sensorComplexKey= $sensorComplexKey,
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
    key: SensorComplexKey,
    context: SensorDataGotToTrackMade.Context,
    timestamps: Iterable[Long]
  ): ProcessingInfo = ProcessingInfo(
    key: SensorComplexKey,
    context.window.getStart,
    context.window.getEnd,
    context.window.maxTimestamp,
    timestamps,
    context.currentProcessingTime,
    context.currentWatermark
  )
}
