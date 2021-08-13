package dope.nathan.movement.data.converter
package logic.state

import dope.nathan.movement.data.model.sensor.Sensor
import org.apache.flink.api.java.functions.KeySelector

case class StateKeyMaker(statePeriod: StatePeriod, stateTimeFrameMarker: StateTimeFrameMarker.type)
    extends KeySelector[Sensor, String] {
  override def getKey(value: Sensor): String = {
    val stateTimeFrame = stateTimeFrameMarker.apply(value.metrics.timestamp, statePeriod)

    s"${value.id}_${stateTimeFrame.toString}"
  }
}
