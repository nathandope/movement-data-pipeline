package dope.nathan.movement.data.converter
package logic.state

import logic.state.track.TrackTimeBoundariesMarker

import dope.nathan.movement.data.model.sensor.Sensor
import org.apache.flink.api.java.functions.KeySelector

case class StateKeyMaker(trackTimeBoundaries: TrackTimeBoundariesMarker) extends KeySelector[Sensor, String] {
  override def getKey(value: Sensor): String = {
    val timeTrackBoundaries = trackTimeBoundaries.mark(value.metrics.timestamp)

    s"${value.id}_${timeTrackBoundaries.toString}"
  }
}
