package dope.nathan.movement.data.converter
package logic.enrichment.track

import dope.nathan.movement.data.model.track.TrackPoint

case class TrackLayout(firstTrackPoint: TrackPoint, lastTrackPoint: TrackPoint, allTrackPoints: Seq[TrackPoint])

object TrackLayout {
  def apply(trackPoints: Seq[TrackPoint]): TrackLayout = {
    val sortedTrackPoints = trackPoints.sortBy(_.timestamp)
    TrackLayout(sortedTrackPoints.head, sortedTrackPoints.last, trackPoints)
  }
}
