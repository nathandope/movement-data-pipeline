package dope.nathan.movement.data.converter
package logic.conversion.track

import dope.nathan.movement.data.model.track.TrackPoint

case class TrackGeopositionBoundaries(firstTrackPoint: TrackPoint, lastTrackPoint: TrackPoint)

object TrackGeopositionBoundaries {
  def safelyApply(trackId: String, trackPoints: Seq[TrackPoint]): Either[String, TrackGeopositionBoundaries] = {
    trackPoints match {
      case Nil =>
        val errorMsg = s"Track with $trackId does not contain the points!"
        Left(errorMsg)

      case points =>
        Right {
          val sortedTrackPoints = points.sortBy(_.timestamp)
          TrackGeopositionBoundaries(sortedTrackPoints.head, sortedTrackPoints.last)
        }
    }
  }
}
