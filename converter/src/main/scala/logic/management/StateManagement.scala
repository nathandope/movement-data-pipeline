package dope.nathan.movement.data.converter
package logic.management

import logic.management.StateManagement.TrackModification

import dope.nathan.movement.data.model.track.Track
import org.apache.flink.api.common.state.ValueState

trait StateManagement {

  protected def updateState(
    track: Track,
    currentTimestamp: Long
  )(implicit state: ValueState[TrackModification]
  ): ValueState[TrackModification] = {
    val freshTrackMod = TrackModification(track, currentTimestamp)
    state.update(freshTrackMod)

    state
  }

}

object StateManagement {
  case class TrackModification(track: Track, lastModified: Long)
}
