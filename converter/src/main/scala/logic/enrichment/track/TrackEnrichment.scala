package dope.nathan.movement.data.converter
package logic.enrichment.track

import logic.operation.SensorComplexKey

import dope.nathan.movement.data.model.track.{Track, TrackPoint, Metrics => TrackMetrics}
import org.joda.time.Interval

object TrackEnrichment {

  implicit class TrackSyntax(track: Track.type) extends TrackMetricsCalculation {

    def apply(sensorComplexKey: SensorComplexKey, trackPoints: Seq[TrackPoint]): Track = {
      val trackLayout = TrackLayout(trackPoints)
      val trackId = generateId(
        sensorComplexKey,
        trackLayout.firstTrackPoint.timestamp,
        trackLayout.lastTrackPoint.timestamp
      )
      val trackMetrics = calculateMetrics(trackLayout)

      track.apply(trackId, sensorComplexKey.carrier, trackMetrics)
    }

    private def generateId(sensorComplexKey: SensorComplexKey, trackStartTime: Long, trackEndTime: Long): String = {
      val trackTimeFrame = new Interval(trackStartTime, trackEndTime)
      s"${sensorComplexKey}_$trackTimeFrame"
    }

    private def calculateMetrics(trackLayout: TrackLayout): TrackMetrics = {
      val (firstLatSinCos, lastLatSinCos, thetaSinCos) = calculateSinCos(
        trackLayout.firstTrackPoint,
        trackLayout.lastTrackPoint
      )
      val (startTime, endTime) = calculateTimeFrame(trackLayout.firstTrackPoint, trackLayout.lastTrackPoint)
      val duration             = calculateDuration(trackLayout.firstTrackPoint, trackLayout.lastTrackPoint)
      val distance             = calculateDistance(firstLatSinCos, lastLatSinCos, thetaSinCos)
      val speed                = calculateSpeed(distance, duration)
      val direction            = calculateDirection(firstLatSinCos, lastLatSinCos, thetaSinCos)

      TrackMetrics(
        startTime,
        endTime,
        duration,
        distance,
        speed,
        direction,
        trackLayout.allTrackPoints
      )
    }
  }

}
