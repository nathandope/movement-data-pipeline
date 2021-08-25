package dope.nathan.movement.data.converter
package logic.enrichment

import dope.nathan.movement.data.model.sensor.Metrics
import dope.nathan.movement.data.model.track.TrackPoint

object TrackPointEnrichment {
  implicit class TrackPointSyntax(trackPoint: TrackPoint.type) {
    def apply(sensorMetrics: Metrics): TrackPoint =
      trackPoint.apply(sensorMetrics.timestamp, sensorMetrics.geoposition)
  }

  def safelyApplyTrackPoints(sensorMetrics: Seq[Metrics]): String => Either[String, Seq[TrackPoint]] =
    errorMsg =>
      sensorMetrics match {
        case Nil => Left(errorMsg)

        case metrics =>
          val trackPoints = metrics.map(TrackPoint.apply(_))
          Right(trackPoints)
      }

}
