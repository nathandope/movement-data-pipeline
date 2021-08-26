package dope.nathan.movement.data.converter
package logic.enrichment.track

import dope.nathan.movement.data.model.event.SensorDataGot
import dope.nathan.movement.data.model.sensor.Metrics
import dope.nathan.movement.data.model.track.TrackPoint

object TrackPointEnrichment {
  implicit class TrackPointSyntax(trackPoint: TrackPoint.type) {
    def apply(sensorMetrics: Metrics): TrackPoint =
      trackPoint.apply(sensorMetrics.timestamp, sensorMetrics.geoposition)

    def safelyConvertFrom(events: Iterable[SensorDataGot]): String => Either[String, Seq[TrackPoint]] =
      errorMsg =>
        events match {
          case Nil => Left(errorMsg)

          case sensorDataGotEvents =>
            val trackPoints = sensorDataGotEvents.map { events =>
              TrackPoint.apply(events.sensor.metrics)
            }
            Right(trackPoints.toSeq)
        }
  }

}
