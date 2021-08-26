package dope.nathan.movement.data.converter
package logic.enrichment.metrics

import logic.enrichment.track.TrackLayout

import dope.nathan.movement.data.model.track.Metrics

object TrackMetricsEnrichment {
  implicit class TrackMetricsSyntax(metrics: Metrics.type) extends MetricsCalculation {
    def apply(trackLayout: TrackLayout): Metrics = {
      val (firstLatSinCos, lastLatSinCos, thetaSinCos) =
        calculateSinCos(trackLayout.firstTrackPoint, trackLayout.lastTrackPoint)

      val (startTime, endTime) = calculateTimeFrame(trackLayout.firstTrackPoint, trackLayout.lastTrackPoint)
      val duration             = calculateDuration(trackLayout.firstTrackPoint, trackLayout.lastTrackPoint)
      val distance             = calculateDistance(firstLatSinCos, lastLatSinCos, thetaSinCos)
      val speed                = calculateSpeed(distance, duration)
      val direction            = calculateDirection(firstLatSinCos, lastLatSinCos, thetaSinCos)

      metrics.apply(
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
