package dope.nathan.movement.data.converter
package logic.state

import dope.nathan.movement.data.model.sensor.Sensor

object StateKeyMaker {

  def apply(sensor: Sensor, statePeriod: StatePeriod, stateTimeFrameMarker: StateTimeFrameMarker.type): String = {
    val stateTimeFrame = stateTimeFrameMarker.apply(sensor.metrics.timestamp, statePeriod)

    s"${sensor.id}_${stateTimeFrame.toString}"
  }

}
