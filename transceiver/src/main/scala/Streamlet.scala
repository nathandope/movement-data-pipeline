package dope.nathan.movement.data.transceiver

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import cloudflow.akkastream.util.scaladsl.HttpServerLogic
import cloudflow.akkastream.{ AkkaServerStreamlet, AkkaStreamletLogic }
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroOutlet
import dope.nathan.movement.data.model.event.SensorDataGot

trait TransceiverShape extends AkkaServerStreamlet {
  @transient val sensorDataGotOut: AvroOutlet[SensorDataGot] = AvroOutlet("sensor-data-got-out")

  final override def shape: StreamletShape = StreamletShape.withOutlets(sensorDataGotOut)
}

trait TransceiverBase extends TransceiverShape {
  import json.SensorDataGotJsonProtocol.SensorDataGotJsonFormat

  final override def createLogic: AkkaStreamletLogic =
    HttpServerLogic.default(this, sensorDataGotOut)

}

object Transceiver extends TransceiverBase
