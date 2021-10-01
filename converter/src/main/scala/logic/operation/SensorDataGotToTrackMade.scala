package dope.nathan.movement.data.converter
package logic.operation

import logic.SensorKey
import logic.enrichment.event.TrackMadeEnrichment

import dope.nathan.movement.data.common.auxiliary.{Logging, ProcessLogging}
import dope.nathan.movement.data.model.event.{SensorDataGot, TrackMade}
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

case object SensorDataGotToTrackMade
    extends ProcessWindowFunction[SensorDataGot, TrackMade, SensorKey, TimeWindow]
    with Logging
    with ProcessLogging {

  import TrackMadeEnrichment.TrackMadeSyntax

  override def process(
    key: SensorKey,
    context: SensorDataGotToTrackMade.Context,
    elements: Iterable[SensorDataGot],
    out: Collector[TrackMade]
  ): Unit = {
    log(stage = "Start", logger.debug)(ProcessingInfo(key, context, elements))

    val maybeOutputEvent = elements match {
      case Nil =>
        val errorMsg = s"There are no elements into ${context.window} with key= $key ."
        logger.warn(errorMsg)
        None

      case inputEvents =>
        val trackMade = TrackMade.apply(key, inputEvents)
        out.collect(trackMade)
        Some(trackMade)
    }

    log("End", logger.debug)(maybeOutputEvent)
  }

}
