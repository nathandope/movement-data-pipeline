package dope.nathan.movement.data.converter
package logic.conversion

import logic.conversion.management.{ StateManagement, TimerManagement }

import dope.nathan.movement.data.model._
import dope.nathan.movement.data.model.event.TrackMade
import dope.nathan.movement.data.model.sensor.Sensor
import org.apache.flink.api.common.state.StateTtlConfig._
import org.apache.flink.api.common.state.{ ValueState, ValueStateDescriptor }
import org.apache.flink.api.common.time.Time
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector
import org.joda.time.Instant
import org.slf4j.{ Logger, LoggerFactory }

object SensorToTrack {
  type Function           = KeyedProcessFunction[String, Sensor, TrackMade]
  type FuncContext        = Function#Context
  type FuncOnTimerContext = Function#OnTimerContext
}

case class SensorToTrack(timeout: Long, timeToLive: Long)
    extends KeyedProcessFunction[String, Sensor, TrackMade]
    with StateManagement
    with TimerManagement {

  import logic.track.TrackSyntax._

  import SensorToTrack._

  @transient lazy val log: Logger = LoggerFactory.getLogger(getClass.getName)

  @transient implicit lazy val state: ValueState[TrackModification] = {
    val descriptor = new ValueStateDescriptor("TrackState", classOf[TrackModification])

    val ttlConfig = newBuilder(Time.minutes(timeToLive))
      .setUpdateType(UpdateType.OnCreateAndWrite)
      .setStateVisibility(StateVisibility.NeverReturnExpired)
      .build

    descriptor.enableTimeToLive(ttlConfig)

    getRuntimeContext.getState(descriptor)
  }

  override def processElement(sensor: Sensor, ctx: FuncContext, out: Collector[TrackMade]): Unit =
    Option(state.value) match {
      case None =>
        val freshTrack = track.Track(ctx.getCurrentKey, sensor)
        val freshState = updateState(freshTrack)
        ctx.setTimer(freshState.value.lastModified, timeout)

      case Some(trackState) =>
        ctx.removeTimer(trackState.lastModified, timeout)

        val updatedTrack = trackState.track.update(sensor.metrics)
        val updatedState = updateState(updatedTrack)
        ctx.setTimer(updatedState.value.lastModified, timeout)
    }

  override def onTimer(timestamp: Long, ctx: FuncOnTimerContext, out: Collector[TrackMade]): Unit =
    state.value match {
      case TrackModification(track, lastModified) if timerIsFired(timestamp, lastModified, timeout) =>
        val errorOrTrack = track.safelyCompleteCalculation

        errorOrTrack.map { completedTrack =>
          val event = TrackMade(completedTrack, Instant.now.getMillis)
          out.collect(event)
        }.left.foreach(log.error)

      case _ =>
    }
}
