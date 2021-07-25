package dope.nathan.movement.data.converter
package logic.track

import dope.nathan.movement.data.model.sensor.{ Sensor, Metrics => SensorMetrics }
import dope.nathan.movement.data.model.track.{ Track, TrackPoint, Metrics => TrackMetrics }

object TrackSyntax {

  implicit class TrackClassSyntax(track: Track) extends TrackMetricsCalculation {

    def update(sensorMetrics: SensorMetrics): Track = {
      val newTrackPoint       = TrackPoint(sensorMetrics.timestamp, sensorMetrics.geoposition)
      val updatedTrackPoints  = track.metrics.trackPoints :+ newTrackPoint
      val updatedTrackMetrics = track.metrics.copy(trackPoints = updatedTrackPoints)

      track.copy(metrics = updatedTrackMetrics)
    }

    def safelyCompleteCalculation: Either[String, Track] = {
      val errorOrBoundaries = TrackBoundaries.safelyApply(track.id, track.metrics.trackPoints)

      errorOrBoundaries.flatMap { boundaryPoints =>
        val metrics = calculateMetrics(boundaryPoints)
        Right(track.copy(metrics = metrics))
      }
    }

    private def calculateMetrics(trackBoundaries: TrackBoundaries): TrackMetrics = {
      val (firstLatSinCos, lastLatSinCos, thetaSinCos) = calculateSinCos(
        trackBoundaries.firstTrackPoint,
        trackBoundaries.lastTrackPoint
      )
      val (startTime, endTime) = calculateTimeFrame(trackBoundaries.firstTrackPoint, trackBoundaries.lastTrackPoint)
      val duration             = calculateDuration(trackBoundaries.firstTrackPoint, trackBoundaries.lastTrackPoint)
      val distance             = calculateDistance(firstLatSinCos, lastLatSinCos, thetaSinCos)
      val speed                = calculateSpeed(distance, duration)
      val direction            = calculateDirection(firstLatSinCos, lastLatSinCos, thetaSinCos)

      track.metrics.copy(
        startTime,
        endTime,
        duration,
        distance,
        speed,
        direction
      )
    }
  }

  implicit class TrackObjectSyntax(track: Track.type) {

    def apply(id: String, sensor: Sensor): Track = {
      val sensorGeoposition = sensor.metrics.geoposition
      val sensorTimestamp   = sensor.metrics.timestamp
      val newPoint          = TrackPoint(sensorTimestamp, sensorGeoposition)

      val trackMetrics = TrackMetrics(
        startTime = sensorTimestamp,
        endTime = 0L,
        duration = 0L,
        distance = 0L,
        speed = 0L,
        direction = sensorGeoposition.direction,
        trackPoints = Seq(newPoint)
      )

      track.apply(id, sensor.carrier, trackMetrics)
    }

  }
}
