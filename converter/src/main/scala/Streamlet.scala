package dope.nathan.movement.data.converter

import logic.WindowConfig
import logic.operation._

import cloudflow.flink.{ FlinkStreamlet, FlinkStreamletContext, FlinkStreamletLogic }
import cloudflow.streamlets.avro.{ AvroInlet, AvroOutlet }
import cloudflow.streamlets.{ ConfigParameter, StreamletShape }
import dope.nathan.movement.data.model.event.{ SensorDataGot, TrackMade }
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

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
        configureEnvironment()

        val windowConfig = WindowConfig.apply
        val dataStream = readStream(sensorDataGotIn)
          .assignTimestampsAndWatermarks(SensorTimestampExtractor(windowConfig.maxTimeDelayOfTrackPoints))
          .keyBy(SensorKeySelector)
          .timeWindow(windowConfig.trackWindowDuration)
          .allowedLateness(windowConfig.trackWindowReleaseTimeout)
          .trigger(SensorTimeWindowTrigger)
          .process(SensorDataGotToTrackMade)

        writeStream(trackMadeOut, dataStream)
      }.left.foreach(log.error("Could not build a graph", _))
    }
  }

  private def configureEnvironment(
    timeCharacteristic: TimeCharacteristic = TimeCharacteristic.EventTime,
    autoWatermarkInterval: Long = 200L
  )(implicit ctx: FlinkStreamletContext
  ): StreamExecutionEnvironment = {
    ctx.env.setStreamTimeCharacteristic(timeCharacteristic)
    ctx.env.getConfig.setAutoWatermarkInterval(autoWatermarkInterval)

    ctx.env
  }
}

object Converter extends ConverterBase with ConverterShape
