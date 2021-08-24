package dope.nathan.movement.data.converter
package logic.conversion

import logic.conversion.management.StateManagement.TrackModification
import logic.conversion.management.{ StateManagement, TimerManagement }

import dope.nathan.movement.data.model._
import dope.nathan.movement.data.model.event.TrackMade
import dope.nathan.movement.data.model.sensor.Sensor
import org.apache.flink.api.common.state.{ ValueState, ValueStateDescriptor }
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector
import org.joda.time.{ DateTime, Instant }
import org.slf4j.{ Logger, LoggerFactory }

object SensorToTrack {
  type Function           = KeyedProcessFunction[String, Sensor, TrackMade]
  type FuncContext        = Function#Context
  type FuncOnTimerContext = Function#OnTimerContext
}

case class SensorToTrack(timeout: Long, timeToLive: Long)
    extends KeyedProcessFunction[String, Sensor, TrackMade]
    with StateManagement
    with TimerManagement
    with StateProcessLogging {

  import SensorToTrack._
  import logic.conversion.track.TrackSyntax._

  @transient lazy val log: Logger = LoggerFactory.getLogger(getClass.getName)

  @transient implicit lazy val state: ValueState[TrackModification] = {
    val descriptor = new ValueStateDescriptor("TrackState", classOf[TrackModification])
    getRuntimeContext.getState(descriptor)
  }

  override def processElement(sensor: Sensor, ctx: FuncContext, out: Collector[TrackMade]): Unit =
    Option(state.value) match {
      case None =>
        logStartProcessMsg("initialization", ctx.getCurrentKey, ctx.timestamp)(log.debug)

        val freshTrack = track.Track(ctx.getCurrentKey, sensor)
        val freshState = updateState(freshTrack, ctx.timestamp)

        logEndProcessMsg("initialized", freshState.value)(log.debug)
        ctx.setTimer(freshState.value.lastModified, timeout)

      case Some(trackState) =>
        logStartProcessMsg("updating", ctx.getCurrentKey, ctx.timestamp)(log.debug)
        ctx.removeTimer(trackState.lastModified, timeout)

        val updatedTrack = trackState.track.update(sensor.metrics)
        val updatedState = updateState(updatedTrack, ctx.timestamp)

        logEndProcessMsg("updated", updatedState.value)(log.debug)
        ctx.setTimer(updatedState.value.lastModified, timeout)
    }

  override def onTimer(timestamp: Long, ctx: FuncOnTimerContext, out: Collector[TrackMade]): Unit = {
    log.debug(s"Timer for ${new DateTime(timestamp)} went off...")

    state.value match {
      case TrackModification(track, lastModified) if timerIsFired(timestamp, lastModified, timeout) =>
        logStartProcessMsg("release", ctx.getCurrentKey, ctx.timestamp)(log.debug)

        val errorOrTrack = track.safelyCompleteCalculation
        val errorOrEvent = errorOrTrack.map(TrackMade(_, Instant.now.getMillis))

        errorOrEvent.fold(log.error, event => {
          out.collect(event)
          logEndProcessMsg("released", event)(log.debug)
        })

      case _ =>
        log.error(s"Something went wrong when the timer went off!")
    }
  }
}
