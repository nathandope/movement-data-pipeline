package dope.nathan.movement.data.converter

import dope.nathan.movement.data.model.event.SensorDataGot
import dope.nathan.movement.data.model.geoposition.{Coordinates, Direction, Geoposition}
import dope.nathan.movement.data.model.sensor.{Metrics, Sensor}
import org.joda.time.{DateTime, Instant}

object Data {

  private val sensor1 = createSensor("id_1", "Choppy") _

  val rowSensor1Track = Seq (
    sensor1("2021-09-01T00:01:00Z", 64.155146, 115.917658, Direction.N),
    sensor1("2021-09-01T00:01:05Z", 64.167866, 115.916566, Direction.N),
    sensor1("2021-09-01T00:01:10Z", 64.174548, 115.915173, Direction.N),
    sensor1("2021-09-01T00:01:15Z", 64.198284, 115.842016, Direction.NW),
    sensor1("2021-09-01T00:01:20Z", 64.202706, 115.832502, Direction.NW),
    sensor1("2021-09-01T00:01:25Z", 64.202706, 115.832502, Direction.NW),
    sensor1("2021-09-01T00:01:30Z", 64.202706, 115.832502, Direction.NW),
    sensor1("2021-09-01T00:01:35Z", 64.202706, 115.832502, Direction.NW),
    sensor1("2021-09-01T00:01:40Z", 64.203855, 115.828833, Direction.NW),
    sensor1("2021-09-01T00:01:45Z", 64.203855, 115.828833, Direction.NW),
    sensor1("2021-09-01T00:01:50Z", 64.203855, 115.828833, Direction.NW),
    sensor1("2021-09-01T00:01:55Z", 64.203855, 115.828833, Direction.NW),
    sensor1("2021-09-01T00:02:00Z", 64.203855, 115.828833, Direction.NW),
    sensor1("2021-09-01T00:02:05Z", 64.203855, 115.828833, Direction.NW),
    sensor1("2021-09-01T00:02:10Z", 64.203855, 115.828833, Direction.NW),
    sensor1("2021-09-01T00:02:15Z", 64.203855, 115.828833, Direction.NW),
    sensor1("2021-09-01T00:02:20Z", 64.203855, 115.828833, Direction.NW),
    sensor1("2021-09-01T00:02:25Z", 64.203855, 115.828833, Direction.NW),
    sensor1("2021-09-01T00:02:30Z", 64.203855, 115.828833, Direction.NW),
    sensor1("2021-09-01T00:02:35Z", 64.203855, 115.828833, Direction.NW)
  )

  val events = rowSensor1Track.map(SensorDataGot(_, Instant.now.getMillis))

  private def createSensor(
    sensorId: String,
    carrier: String
  )(
    dateTimeStr: String,
    lat: Double,
    lon: Double,
    direction: Direction
  ): Sensor = {
    val coordinates = Coordinates(lat, lon)
    val geoposition = Geoposition(coordinates, direction)
    val dateTime    = DateTime.parse(dateTimeStr)
    val metrics     = Metrics(dateTime.getMillis, geoposition)

    Sensor(sensorId, carrier, metrics)
  }

}
