package dope.nathan.movement.data.converter
package logic.operation

import dope.nathan.movement.data.common.auxiliary.BaseLogging
import dope.nathan.movement.data.model.event.SensorDataGot
import org.apache.flink.api.java.functions.KeySelector

case class SensorComplexKey(id: String, carrier: String) {
  override def toString: String = s"${id}_$carrier"
}

case object SensorKeySelector extends KeySelector[SensorDataGot, SensorComplexKey] with BaseLogging {
  override def getKey(event: SensorDataGot): SensorComplexKey = {
    val key = SensorComplexKey(event.sensor.id, event.sensor.carrier)
    logger.debug(s"Generated sensor complex key= $key .")
    key
  }

}
