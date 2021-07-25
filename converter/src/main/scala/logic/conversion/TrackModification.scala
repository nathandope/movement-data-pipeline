package dope.nathan.movement.data.converter
package logic.conversion

import dope.nathan.movement.data.model.track.Track
import org.joda.time.Instant

case class TrackModification(track: Track, lastModified: Long = 0L)

object TrackModification {
  def apply(track: Track): TrackModification =
    TrackModification(track, Instant.now.getMillis)
}
