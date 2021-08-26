package dope.nathan.movement.data.transceiver
package json

import dope.nathan.movement.data.model.Sensor
import dope.nathan.movement.data.model.event.SensorDataGot
import spray.json._

object SensorDataGotJsonProtocol extends DefaultJsonProtocol {
  import SensorJsonProtocol.SensorJsonFormat

  implicit object SensorDataGotJsonFormat extends RootJsonFormat[SensorDataGot] {

    override def write(obj: SensorDataGot): JsObject = JsObject(
      "sensor"    -> obj.sensor.toJson,
      "eventTime" -> JsNumber(obj.eventTime)
    )

    override def read(json: JsValue): SensorDataGot =
      json.asJsObject.getFields("sensor", "eventTime") match {
        case Seq(sensor, JsNumber(eventTime)) =>
          SensorDataGot(
            sensor.convertTo[Sensor],
            eventTime.longValue
          )

        case unknown =>
          throw DeserializationException(s"SensorDataGot expected instead of $unknown .")
      }
  }
}
