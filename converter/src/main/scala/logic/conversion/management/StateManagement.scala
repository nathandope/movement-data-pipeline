package dope.nathan.movement.data.converter
package logic.conversion.management

import logic.conversion.TrackModification

import dope.nathan.movement.data.model.track.Track
import org.apache.flink.api.common.state.ValueState

trait StateManagement {

  protected def updateState(
    track: Track
  )(implicit state: ValueState[TrackModification]
  ): ValueState[TrackModification] = {
    val freshTrackMod = TrackModification(track)
    state.update(freshTrackMod)
    state
  }

}
