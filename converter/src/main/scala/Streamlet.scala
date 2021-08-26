package dope.nathan.movement.data.converter

import logic.config.{ FlinkConfiguration, WindowConfig }
import logic.operation._

import cloudflow.flink.{ FlinkStreamlet, FlinkStreamletLogic }
import cloudflow.streamlets.avro.{ AvroInlet, AvroOutlet }
import cloudflow.streamlets.{ ConfigParameter, StreamletShape }
import dope.nathan.movement.data.model.event.{ SensorDataGot, TrackMade }
import org.apache.flink.api.scala.createTypeInformation

trait ConverterShape extends FlinkStreamlet {
  @transient val sensorDataGotIn: AvroInlet[SensorDataGot] = AvroInlet("sensor-data-got-in")
  @transient val trackMadeOut: AvroOutlet[TrackMade]       = AvroOutlet("track-made-out")

  override def shape(): StreamletShape = StreamletShape(sensorDataGotIn).withOutlets(trackMadeOut)
}

trait ConverterBase extends ConverterShape {

  override def configParameters: Vector[ConfigParameter] = WindowConfig.allParameters

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic {
    override def buildExecutionGraph(): Unit = {
      import scala.util.control.Exception._

      catching(nonFatalCatcher).either {
        val windowConfig = WindowConfig.apply

        FlinkConfiguration(context.env)
          .tune(windowConfig.autoWatermarkIntervalParameter)

        val dataStream = readStream(sensorDataGotIn)
          .assignTimestampsAndWatermarks(SensorTimestampExtractor(windowConfig.maxTimeDelayOfTrackPoints))
          .keyBy(SensorKeySelector)
          .timeWindow(windowConfig.trackWindowDuration)
          .allowedLateness(windowConfig.trackWindowReleaseTimeout)
          .process(SensorDataGotToTrackMade)

        writeStream(trackMadeOut, dataStream)
      }.left.foreach(log.error("Could not build a graph", _))
    }
  }
}

object Converter extends ConverterBase with ConverterShape
