package dope.nathan.movement.data.converter
package logic.enrichment.event

import logic.SensorKey
import logic.enrichment.track.{ TrackEnrichment, TrackPointEnrichment }

import dope.nathan.movement.data.model.Track
import dope.nathan.movement.data.model.event.{ SensorDataGot, TrackMade }
import dope.nathan.movement.data.model.track.TrackPoint
import org.joda.time.Instant

object TrackMadeEnrichment {
  import TrackEnrichment.TrackSyntax
  import TrackPointEnrichment.TrackPointSyntax

  implicit class TrackMadeSyntax(trackMade: TrackMade.type) {
    def apply(sensorKey: SensorKey, sensorDataGotEvents: Iterable[SensorDataGot]): TrackMade = {
      val trackPoints = sensorDataGotEvents.map(event => TrackPoint.apply(event.sensor.metrics))
      val track       = Track.apply(sensorKey, trackPoints.toSeq)

      trackMade.apply(track, Instant.now.getMillis)
    }
  }
}
