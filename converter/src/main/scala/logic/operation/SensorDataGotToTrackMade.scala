package dope.nathan.movement.data.converter
package logic.operation

import logic.WindowProcessLogging

import dope.nathan.movement.data.model.Track
import dope.nathan.movement.data.model.event.{ SensorDataGot, TrackMade }
import dope.nathan.movement.data.model.track.TrackPoint
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import org.joda.time.Instant

case object SensorDataGotToTrackMade
    extends ProcessWindowFunction[SensorDataGot, TrackMade, SensorComplexKey, TimeWindow]
    with WindowProcessLogging {

  import logic.enrichment.track.TrackEnrichment._
  import logic.enrichment.track.TrackPointEnrichment._

  override def process(
    key: SensorComplexKey,
    context: SensorDataGotToTrackMade.Context,
    elements: Iterable[SensorDataGot],
    out: Collector[TrackMade]
  ): Unit = {
    val logByStage = logProcess(key, context, elements.map(_.sensor.metrics.timestamp), log.debug)

    logByStage("Start")

    val maybeTrackPoints = TrackPoint.safelyConvertFrom(elements) {
      s"""Could not convert the sensor metrics to the track points, cause
           |there are no events into ${context.window} with key= $key .""".stripMargin
    }

    maybeTrackPoints fold (log.error, trackPoints => {
      val track     = Track.apply(key, trackPoints)
      val trackMade = TrackMade(track, Instant.now.getMillis)

      out.collect(trackMade)

      logByStage("End")
    })
  }

}
