package dope.nathan.movement.data.converter

import dope.nathan.movement.data.model.event.SensorDataGot
import dope.nathan.movement.data.model.geoposition.{ Coordinates, Direction, Geoposition }
import dope.nathan.movement.data.model.sensor.{ Metrics, Sensor }
import org.joda.time.{ DateTime, Instant }

object Data {

  private val fillASensor = createSensor("id_A", "Choppy") _
  private val fillBSensor = createSensor("id_B", "Hugger") _

  private val rawASensorTrack = Seq(
    fillASensor("2021-09-01T00:01:00Z", 64.155146, 115.917658, Direction.N),
    fillASensor("2021-09-01T00:01:05Z", 64.167866, 115.916566, Direction.N),
    fillASensor("2021-09-01T00:01:10Z", 64.174548, 115.915173, Direction.N),
    fillASensor("2021-09-01T00:01:15Z", 64.198284, 115.842016, Direction.NW),
    fillASensor("2021-09-01T00:01:20Z", 64.202706, 115.832502, Direction.NW),
    fillASensor("2021-09-01T00:01:25Z", 64.202706, 115.832502, Direction.NW),
    fillASensor("2021-09-01T00:01:30Z", 64.202706, 115.832502, Direction.NW),
    fillASensor("2021-09-01T00:01:35Z", 64.202706, 115.832502, Direction.NW),
    fillASensor("2021-09-01T00:01:40Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:01:45Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:01:50Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:01:55Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:00Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:05Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:10Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:15Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:20Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:25Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:30Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:35Z", 64.203855, 115.828833, Direction.NW)
  )

  private val rawBSensorTrack = Seq(
    fillBSensor("2021-09-01T00:01:01Z", 64.155146, 115.917658, Direction.N),
    fillBSensor("2021-09-01T00:01:06Z", 64.167866, 115.916566, Direction.N),
    fillBSensor("2021-09-01T00:01:11Z", 64.174548, 115.915173, Direction.N),
    fillBSensor("2021-09-01T00:01:16Z", 64.198284, 115.842016, Direction.NW),
    fillBSensor("2021-09-01T00:01:21Z", 64.202706, 115.832502, Direction.NW),
    fillBSensor("2021-09-01T00:01:26Z", 64.202706, 115.832502, Direction.NW),
    fillBSensor("2021-09-01T00:01:31Z", 64.202706, 115.832502, Direction.NW),
    fillBSensor("2021-09-01T00:01:36Z", 64.202706, 115.832502, Direction.NW),
    fillBSensor("2021-09-01T00:01:41Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:01:46Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:01:51Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:01:56Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:01Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:06Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:11Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:16Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:21Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:26Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:31Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:36Z", 64.203855, 115.828833, Direction.NW)
  )

  val rawSensorsTrack: Seq[Sensor] = rawASensorTrack ++ rawBSensorTrack

  val inputEvents: Seq[SensorDataGot] =
    rawSensorsTrack
      .map(SensorDataGot(_, Instant.now.getMillis))

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
