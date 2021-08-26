package dope.nathan.movement.data.transceiver
package json

import dope.nathan.movement.data.model.geoposition.Coordinates
import spray.json.{ DeserializationException, JsNumber, JsObject, JsValue, RootJsonFormat }

object CoordinatesJsonProtocol {
  implicit object CoordinatesJsonFormat extends RootJsonFormat[Coordinates] {

    override def write(obj: Coordinates): JsObject = JsObject(
      "lat" -> JsNumber(obj.lat),
      "lon" -> JsNumber(obj.lon)
    )

    override def read(json: JsValue): Coordinates =
      json.asJsObject.getFields("lat", "lon") match {
        case Seq(JsNumber(lat), JsNumber(lon)) =>
          Coordinates(
            lat.doubleValue,
            lon.doubleValue
          )

        case unknown =>
          throw DeserializationException(s"Coordinates expected instead of $unknown .")
      }
  }
}
