package dope.nathan.movement.data.transceiver
package json

import dope.nathan.movement.data.model.Geoposition
import dope.nathan.movement.data.model.sensor.Metrics
import spray.json.{DeserializationException, JsNumber, JsObject, JsValue, RootJsonFormat, enrichAny}

object MetricsJsonProtocol {

  import GeopositionJsonProtocol.GeopositionJsonFormat

  implicit object MetricsJsonFormat extends RootJsonFormat[Metrics] {

    override def write(obj: Metrics): JsObject = JsObject(
      "timestamp" -> JsNumber(obj.timestamp),
      "geoposition"   -> obj.geoposition.toJson
    )

    override def read(json: JsValue): Metrics =
      json.asJsObject.getFields("timestamp", "geoposition") match {
        case Seq(JsNumber(timestamp), geoposition) =>
          Metrics(
            timestamp.longValue,
            geoposition.convertTo[Geoposition]
          )

        case unknown =>
          throw DeserializationException(s"Metrics expected instead of $unknown .")
      }
  }
}
