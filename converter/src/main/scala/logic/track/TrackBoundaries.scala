package dope.nathan.movement.data.converter
package logic.track

import dope.nathan.movement.data.model.track.TrackPoint

case class TrackBoundaries(firstTrackPoint: TrackPoint, lastTrackPoint: TrackPoint)

object TrackBoundaries {
  def safelyApply(trackId: String, trackPoints: Seq[TrackPoint]): Either[String, TrackBoundaries] = {
    trackPoints match {
      case Nil =>
        val errorMsg = s"Track with $trackId does not contain the points!"
        Left(errorMsg)

      case points =>
        Right {
          val sortedTrackPoints = points.sortBy(_.timestamp)
          TrackBoundaries(sortedTrackPoints.head, sortedTrackPoints.last)
        }
    }
  }
}
