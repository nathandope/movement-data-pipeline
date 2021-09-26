package dope.nathan.movement.data.converter
package logic.enrichment.track

import dope.nathan.movement.data.model.sensor.Metrics
import dope.nathan.movement.data.model.track.TrackPoint

object TrackPointEnrichment {
  implicit class TrackPointSyntax(trackPoint: TrackPoint.type) {
    def apply(sensorMetrics: Metrics): TrackPoint =
      trackPoint.apply(sensorMetrics.timestamp, sensorMetrics.geoposition)
  }

}
