package dope.nathan.movement.data.converter
package logic
import logic.config.{FlinkConfig, FlinkSetup, WindowConfig}
import logic.operation.{SensorDataGotToTrackMade, SensorKeySelector, SensorTimestampExtractor}

import cloudflow.flink.{FlinkStreamletContext, FlinkStreamletLogic}
import cloudflow.streamlets.avro.{AvroInlet, AvroOutlet}
import dope.nathan.movement.data.common.auxiliary.ExceptionManagement
import dope.nathan.movement.data.model.event.{SensorDataGot, TrackMade}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.DataStream

import scala.util.control.Exception.{catching, nonFatalCatcher}

case class ConverterLogic(
  flinkConfig: FlinkConfig,
  sensorDataGotIn: AvroInlet[SensorDataGot],
  trackMadeOut: AvroOutlet[TrackMade]
)(implicit override val context: FlinkStreamletContext)
    extends FlinkStreamletLogic
    with ExceptionManagement {

  override def buildExecutionGraph(): Unit =
    catching(nonFatalCatcher).either {
      FlinkSetup(context.env)
        .tune(flinkConfig.environmentConfig)

      val sensorDataGotStream = readStream(sensorDataGotIn)
      val trackMadeStream     = processStream(flinkConfig.windowConfig, sensorDataGotStream)

      writeStream(trackMadeOut, trackMadeStream)

    }.left.foreach(castAndThrow("Could not build a graph"))

  private def processStream(
    config: WindowConfig,
    sensorDataGotStream: DataStream[SensorDataGot]
  ): DataStream[TrackMade] = {
    val timestampExtractor = SensorTimestampExtractor(config.maxTimeDelayOfTrackPoints)

    sensorDataGotStream
      .assignTimestampsAndWatermarks(timestampExtractor)
      .keyBy(SensorKeySelector)
      .timeWindow(config.trackWindowDuration)
      .allowedLateness(config.trackWindowReleaseTimeout)
      .process(SensorDataGotToTrackMade)
  }

}
