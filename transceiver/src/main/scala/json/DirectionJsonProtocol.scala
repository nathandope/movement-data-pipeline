package dope.nathan.movement.data.transceiver
package json

import dope.nathan.movement.data.model.geoposition.Direction
import spray.json._

object DirectionJsonProtocol {
  implicit object DirectionJsonFormat extends RootJsonFormat[Direction] {

    override def write(obj: Direction): JsValue = JsString(obj.name)

    override def read(json: JsValue): Direction =
      json match {
        case jsString: JsString =>
          matchDirection(jsString)

        case unknown =>
          throw DeserializationException(s"Direction expected instead of a $unknown .")
      }
  }

  private def matchDirection: JsString => Direction = _.value match {
    case "N"   => Direction.N
    case "NNE" => Direction.NNE
    case "NE"  => Direction.NE
    case "ENE" => Direction.ENE
    case "E"   => Direction.E
    case "ESE" => Direction.ESE
    case "SE"  => Direction.SE
    case "SSE" => Direction.SSE
    case "S"   => Direction.S
    case "SSW" => Direction.SSW
    case "SW"  => Direction.SW
    case "WSW" => Direction.WSW
    case "W"   => Direction.W
    case "WNW" => Direction.WNW
    case "NW"  => Direction.NW
    case "NNW" => Direction.NNW
    case _     => Direction.Zero
  }
}
