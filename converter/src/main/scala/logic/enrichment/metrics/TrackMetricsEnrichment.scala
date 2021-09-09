package dope.nathan.movement.data.converter
package logic.enrichment.metrics

import logic.enrichment.track.TrackLayout

import dope.nathan.movement.data.model.track.Metrics

object TrackMetricsEnrichment {
  implicit class TrackMetricsSyntax(metrics: Metrics.type) extends MetricsCalculation {
    def apply(trackLayout: TrackLayout): Metrics = {
      import trackLayout._

      val trackTrigonometry    = calculateTrackTrigonometry(firstTrackPoint, lastTrackPoint)
      val (startTime, endTime) = calculateTimeFrame(firstTrackPoint, lastTrackPoint)
      val duration             = calculateDuration(firstTrackPoint, lastTrackPoint)
      val distance             = calculateDistance(trackTrigonometry)
      val speed                = calculateSpeed(distance, duration)
      val direction            = calculateDirection(firstTrackPoint, lastTrackPoint, trackTrigonometry)

      metrics.apply(
        startTime,
        endTime,
        duration,
        distance,
        speed,
        direction,
        allTrackPoints
      )
    }
  }

}
