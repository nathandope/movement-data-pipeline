package dope.nathan.movement.data.converter

import logic.ConverterLogic
import logic.config.FlinkConfig

import cloudflow.flink.{ FlinkStreamlet, FlinkStreamletLogic }
import cloudflow.streamlets.avro.{ AvroInlet, AvroOutlet }
import cloudflow.streamlets.{ ConfigParameter, StreamletShape }
import dope.nathan.movement.data.model.event.{ SensorDataGot, TrackMade }

trait ConverterOpenings {
  @transient val sensorDataGotIn: AvroInlet[SensorDataGot] =
    AvroInlet("sensor-data-got-in")

  @transient val trackMadeOut: AvroOutlet[TrackMade] =
    AvroOutlet("track-made-out")
}

trait ConverterShape extends FlinkStreamlet with ConverterOpenings {
  override def shape(): StreamletShape =
    StreamletShape(sensorDataGotIn).withOutlets(trackMadeOut)
}

trait ConverterBase extends ConverterShape {
  override def configParameters: Vector[ConfigParameter] =
    FlinkConfig.allParameters

  override protected def createLogic(): FlinkStreamletLogic =
    new ConverterLogic(FlinkConfig.apply)
}

object Converter extends ConverterBase
