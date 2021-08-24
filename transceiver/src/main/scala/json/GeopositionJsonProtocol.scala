package dope.nathan.movement.data.tracker
package json

import dope.nathan.movement.data.model.geoposition.{ Coordinates, Direction, Geoposition }
import spray.json.{ enrichAny, DeserializationException, JsObject, JsValue, RootJsonFormat }

object GeopositionJsonProtocol {
  import DirectionJsonProtocol.DirectionJsonFormat

  import CoordinatesJsonProtocol.CoordinatesJsonFormat

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
