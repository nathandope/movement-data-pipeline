package dope.nathan.movement.data.converter
package logic

import logic.config.{ FlinkConfig, FlinkSetup, WindowConfig }
import logic.operation.{ SensorDataGotToTrackMade, SensorKeySelector, SensorTimestampExtractor }

import cloudflow.flink.{ FlinkStreamletContext, FlinkStreamletLogic }
import dope.nathan.movement.data.common.auxiliary.ExceptionManagement
import dope.nathan.movement.data.model.event.{ SensorDataGot, TrackMade }
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.DataStream

case class ConverterLogic(flinkConfig: FlinkConfig)(implicit override val context: FlinkStreamletContext)
    extends FlinkStreamletLogic
    with ConverterOpenings
    with ExceptionManagement {

  import ConverterLogic._

  FlinkSetup(context.env).tune(flinkConfig.environmentConfig)

  override def buildExecutionGraph(): Unit =
    safelyBuildExecutionGraph.fold(throw _, identity)

  private def safelyBuildExecutionGraph =
    safely {
      val sensorDataGotStream = readStream(sensorDataGotIn)
      val trackMadeStream     = processStream(flinkConfig.windowConfig, sensorDataGotStream)

      writeStream(trackMadeOut, trackMadeStream)
    }(Some("Could not build a graph"))

}

object ConverterLogic extends Serializable {
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
