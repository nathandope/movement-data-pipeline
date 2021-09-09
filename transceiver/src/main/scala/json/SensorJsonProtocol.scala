package dope.nathan.movement.data.transceiver
package json

import dope.nathan.movement.data.model.Sensor
import dope.nathan.movement.data.model.sensor.Metrics
import spray.json._

object SensorJsonProtocol extends DefaultJsonProtocol {
  import MetricsJsonProtocol.MetricsJsonFormat

  implicit object SensorJsonFormat extends RootJsonFormat[Sensor] {

    override def write(obj: Sensor): JsObject = JsObject(
      "id"      -> JsString(obj.id),
      "carrier" -> JsString(obj.carrier),
      "metrics" -> obj.metrics.toJson
    )

    override def read(json: JsValue): Sensor =
      json.asJsObject.getFields("id", "carrier", "metrics") match {
        case Seq(JsString(id), JsString(carrier), metrics) =>
          Sensor(
            id,
            carrier,
            metrics.convertTo[Metrics]
          )

        case unknown =>
          throw DeserializationException(s"Sensor expected instead of $unknown .")
      }
  }
}
