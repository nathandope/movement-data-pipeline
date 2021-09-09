package dope.nathan.movement.data.transceiver
package json

import dope.nathan.movement.data.model.Geoposition
import dope.nathan.movement.data.model.geoposition.{Coordinates, Direction}
import spray.json.{DeserializationException, JsObject, JsValue, RootJsonFormat, enrichAny}

object GeopositionJsonProtocol {
  import CoordinatesJsonProtocol.CoordinatesJsonFormat
  import DirectionJsonProtocol.DirectionJsonFormat

  implicit object GeopositionJsonFormat extends RootJsonFormat[Geoposition] {

    override def write(obj: Geoposition): JsObject = JsObject(
      "coordinates" -> obj.coordinates.toJson,
      "direction"   -> obj.direction.toJson
    )

    override def read(json: JsValue): Geoposition =
      json.asJsObject.getFields("coordinates", "direction") match {
        case Seq(coordinates, direction) =>
          Geoposition(
            coordinates.convertTo[Coordinates],
            direction.convertTo[Direction]
          )

        case unknown =>
          throw DeserializationException(s"Geoposition expected instead of $unknown .")
      }
  }
}
