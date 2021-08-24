package dope.nathan.movement.data.converter

import dope.nathan.movement.data.model.sensor.Sensor
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.windowing.time.Time

case class TimestampExtractor(maxOutOfOrderness: Time)
    extends BoundedOutOfOrdernessTimestampExtractor[Sensor](maxOutOfOrderness) {
  override def extractTimestamp(element: Sensor): Long = element.metrics.timestamp
}
