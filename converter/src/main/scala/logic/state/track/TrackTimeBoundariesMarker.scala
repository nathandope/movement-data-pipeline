package dope.nathan.movement.data.converter
package logic.state.track

import logic.state.track.TrackTimeBoundariesMarker.TrackTimeBoundaries

import org.joda.time.{ DateTime, Interval }

object TrackTimeBoundariesMarker extends Serializable { type TrackTimeBoundaries = Interval }

case class TrackTimeBoundariesMarker(trackDuration: TrackDuration)
    extends TimeBoundariesMarkerBase[Long, TrackTimeBoundaries] {

  def mark(sensorTimestamp: Long): TrackTimeBoundaries = {
    val boundariesMarker              = resolveBoundariesMarker(trackDuration)
    val (leftBoundary, rightBoundary) = boundariesMarker.mark(new DateTime(sensorTimestamp))

    new TrackTimeBoundaries(leftBoundary, rightBoundary)
  }

  private def resolveBoundariesMarker: TrackDuration => TimeBoundariesMarker = {
    case Daily    => DailyBoundariesMarker
    case Hourly   => HourlyBoundariesMarker
    case Minutely => MinutelyBoundariesMarker
  }

}
