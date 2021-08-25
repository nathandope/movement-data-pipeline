package dope.nathan.movement.data.converter
package logic.operation

import dope.nathan.movement.data.model.event.SensorDataGot
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.windowing.time.Time

case class SensorTimestampExtractor(maxTimeDelayOfTrackPoints: Time)
    extends BoundedOutOfOrdernessTimestampExtractor[SensorDataGot](maxTimeDelayOfTrackPoints) {

  override def extractTimestamp(element: SensorDataGot): Long =
    element.sensor.metrics.timestamp

}
