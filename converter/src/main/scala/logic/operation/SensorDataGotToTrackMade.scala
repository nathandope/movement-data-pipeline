package dope.nathan.movement.data.converter
package logic.operation

import dope.nathan.movement.data.model.event.{ SensorDataGot, TrackMade }
import dope.nathan.movement.data.model.track.Track
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.api.scala.typeutils.Types
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import org.joda.time.Instant
import org.slf4j.{ Logger, LoggerFactory }

case object SensorDataGotToTrackMade
    extends ProcessWindowFunction[SensorDataGot, TrackMade, SensorComplexKey, TimeWindow] {

  import logic.enrichment.TrackPointEnrichment._
  import logic.enrichment.track.TrackEnrichment._

  lazy val log: Logger = LoggerFactory.getLogger(getClass)

  override def process(
    key: SensorComplexKey,
    context: Context,
    events: Seq[SensorDataGot],
    out: Collector[TrackMade]
  ): Unit = {
    val sensorMetrics = events.map(_.sensor.metrics)
    val maybeTrackPoints = safelyApplyTrackPoints(sensorMetrics) {
      s"""Could not convert the sensor metrics to the track points, cause
           |there are no events into ${context.window} with key= $key .""".stripMargin
    }

    maybeTrackPoints fold (log.error, trackPoints => {
      val track     = Track.apply(key, trackPoints)
      val trackMade = TrackMade(track, Instant.now.getMillis)

      out.collect(trackMade)
      logEndProcessMsg("released", trackMade)(log.debug)
    })
  }
}

/** Функция подсчета WindowProcessFunction, которая различает первые результаты и обновления.*/
class UpdatingWindowCountFunction
    extends ProcessWindowFunction[SensorDataGot, TrackMade, SensorComplexKey, TimeWindow] {
  override def process(
    id: String,
    ctx: Context,
    elements: Iterable[SensorDataGot],
    out: Collector[(String, Long, Int, String)]
  ): Unit = {
    // Подсчет числа элементов.
    val cnt = elements.count(_ => true)
    // Проверка, является ли эта обработка окна первой.
    val isUpdate = ctx.windowState.getState(new ValueStateDescriptor[Boolean]("isUpdate", Types.of[Boolean]))
    if (!isUpdate.value()) {
      // Первая обработка, выпуск первого результата.
      out.collect((id, ctx.window.getEnd, cnt, "first"))
      isUpdate.update(true)
    } else {
      // Не первая обработка, выпуск обновления.
      out.collect((id, ctx.window.getEnd, cnt, "update"))
    }
  }
}
