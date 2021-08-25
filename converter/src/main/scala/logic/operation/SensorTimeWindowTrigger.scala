package dope.nathan.movement.data.converter
package logic.operation

import dope.nathan.movement.data.model.event.SensorDataGot
import dope.nathan.movement.data.model.sensor.Sensor
import org.apache.flink.streaming.api.windowing.triggers.{Trigger, TriggerResult}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

case object SensorTimeWindowTrigger extends Trigger[SensorDataGot, TimeWindow] {

  override def onElement(
    element: Sensor,
    timestamp: Long,
    window: TimeWindow,
    ctx: Trigger.TriggerContext
  ): TriggerResult = {

  }

  override def onProcessingTime(time: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {

  }

  override def onEventTime(time: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {

  }

  override def clear(window: TimeWindow, ctx: Trigger.TriggerContext): Unit = {

  }

}
