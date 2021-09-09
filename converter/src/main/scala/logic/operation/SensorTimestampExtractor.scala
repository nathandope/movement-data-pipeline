package dope.nathan.movement.data.converter
package logic.operation

import logic.enrichment.flink.BoundedOutOfOrdernessTimestampExtractor

import dope.nathan.movement.data.common.auxiliary.BaseLogging
import dope.nathan.movement.data.model.event.SensorDataGot
import org.apache.flink.api.common.time.Time
import org.joda.time.DateTime

case class SensorTimestampExtractor(maxTimeDelayOfTrackPoints: Time)
    extends BoundedOutOfOrdernessTimestampExtractor[SensorDataGot](maxTimeDelayOfTrackPoints)
    with BaseLogging {

  override def extractTimestamp(element: SensorDataGot): Long = {
    val timestamp = element.sensor.metrics.timestamp
    logger.debug(s"Extracted sensor timestamp= $timestamp (${new DateTime(timestamp)}).")
    timestamp
  }

}
