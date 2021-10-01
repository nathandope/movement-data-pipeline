package dope.nathan.movement.data.converter
package logic.enrichment.track

import logic.SensorKey

import dope.nathan.movement.data.model.Track
import dope.nathan.movement.data.model.track.{Metrics, TrackPoint}
import org.joda.time.Interval

object TrackEnrichment {
  import logic.enrichment.metrics.TrackMetricsEnrichment._

  implicit class TrackSyntax(track: Track.type) {
    def apply(sensorKey: SensorKey, trackPoints: Seq[TrackPoint]): Track = {
      val trackLayout = TrackLayout(trackPoints)
      val trackId = generateId(
        sensorKey,
        trackLayout.firstTrackPoint.timestamp,
        trackLayout.lastTrackPoint.timestamp
      )
      val trackMetrics = Metrics.apply(trackLayout)

      track.apply(trackId, carrier = sensorKey._2, trackMetrics)
    }

    private def generateId(sensorKey: SensorKey, trackStartTime: Long, trackEndTime: Long): String = {
      val trackTimeFrame = new Interval(trackStartTime, trackEndTime)
      val (id, carrier)  = (sensorKey._1, sensorKey._2)

      s"${id}_${carrier}_$trackTimeFrame"
    }
  }

}
