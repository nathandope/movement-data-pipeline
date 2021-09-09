package dope.nathan.movement.data.converter
package logic.operation

import dope.nathan.movement.data.common.auxiliary.{BaseLogging, ProcessLogging}
import dope.nathan.movement.data.model.Track
import dope.nathan.movement.data.model.event.{SensorDataGot, TrackMade}
import dope.nathan.movement.data.model.track.TrackPoint
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import org.joda.time.Instant

case object SensorDataGotToTrackMade
    extends ProcessWindowFunction[SensorDataGot, TrackMade, SensorComplexKey, TimeWindow]
    with BaseLogging
    with ProcessLogging[ProcessingInfo] {

  import logic.enrichment.track.TrackEnrichment._
  import logic.enrichment.track.TrackPointEnrichment._

  override def process(
    key: SensorComplexKey,
    context: SensorDataGotToTrackMade.Context,
    elements: Iterable[SensorDataGot],
    out: Collector[TrackMade]
  ): Unit = {
    val processingInfo = ProcessingInfo(key, context, elements.map(_.sensor.metrics.timestamp))
    val logBy          = log(_, processingInfo, logger.debug)

    logBy("Start")

    val maybeTrackPoints = TrackPoint.safelyConvertFrom(elements) {
      s"Could not convert the sensor metrics to the track points, cause " +
        s"there are no events into ${context.window} with key= $key ."
    }

    maybeTrackPoints.fold(
      errorMsg => logger.error(errorMsg),
      trackPoints => {
        val track     = Track.apply(key, trackPoints)
        val trackMade = TrackMade(track, Instant.now.getMillis)
        logBy("End")
        out.collect(trackMade)
      }
    )
  }

}
