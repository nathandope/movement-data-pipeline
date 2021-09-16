package dope.nathan.movement.data.converter
package logic
import logic.config.{ FlinkConfig, FlinkSetup, WindowConfig }
import logic.operation.{ SensorDataGotToTrackMade, SensorKeySelector, SensorTimestampExtractor }

import cloudflow.flink.{ FlinkStreamletContext, FlinkStreamletLogic }
import dope.nathan.movement.data.common.auxiliary.ExceptionManagement
import dope.nathan.movement.data.model.event.{ SensorDataGot, TrackMade }
import org.apache.flink.api.common.JobExecutionResult
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{ DataStream, StreamExecutionEnvironment }

case class ConverterLogic(flinkConfig: FlinkConfig)(implicit override val context: FlinkStreamletContext)
    extends FlinkStreamletLogic
    with ConverterOpenings
    with ExceptionManagement {

  FlinkSetup(context.env).tune(flinkConfig.environmentConfig)

  override def buildExecutionGraph(): Unit = {
    val sensorDataGotStream = readStream(sensorDataGotIn)
    val trackMadeStream = ConverterLogic.processStream(
      flinkConfig.windowConfig,
      sensorDataGotStream
    )

    writeStream(trackMadeOut, trackMadeStream)
  }

  override def executeStreamingQueries(env: StreamExecutionEnvironment): JobExecutionResult = {
    val exceptionOrGraphIsBuilt = safely {
      buildExecutionGraph()
    }(Some("Could not build a graph"))

    val exceptionOrExecResult = safely {
      env.execute(s"Executing $streamletRef")
    }(Some("Could not get execution result"))

    exceptionOrGraphIsBuilt
      .flatMap(_ => exceptionOrExecResult)
      .fold(throw _, identity)
  }
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
