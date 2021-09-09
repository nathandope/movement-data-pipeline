package dope.nathan.movement.data.converter
package logic.enrichment.track

import logic.operation.SensorComplexKey

import dope.nathan.movement.data.model.Track
import dope.nathan.movement.data.model.track.{Metrics, TrackPoint}
import org.joda.time.Interval

object TrackEnrichment {
  import logic.enrichment.metrics.TrackMetricsEnrichment._

  implicit class TrackSyntax(track: Track.type) {
    def apply(sensorComplexKey: SensorComplexKey, trackPoints: Seq[TrackPoint]): Track = {
      val trackLayout = TrackLayout(trackPoints)
      val trackId = generateId(
        sensorComplexKey,
        trackLayout.firstTrackPoint.timestamp,
        trackLayout.lastTrackPoint.timestamp
      )
      val trackMetrics = Metrics.apply(trackLayout)

      track.apply(trackId, sensorComplexKey.carrier, trackMetrics)
    }

    private def generateId(sensorComplexKey: SensorComplexKey, trackStartTime: Long, trackEndTime: Long): String = {
      val trackTimeFrame = new Interval(trackStartTime, trackEndTime)
      s"${sensorComplexKey}_$trackTimeFrame"
    }
  }

}
