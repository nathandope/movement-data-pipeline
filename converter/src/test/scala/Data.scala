package dope.nathan.movement.data.converter

import dope.nathan.movement.data.model.event.SensorDataGot
import dope.nathan.movement.data.model.geoposition.{ Coordinates, Direction }
import dope.nathan.movement.data.model.sensor.Metrics
import dope.nathan.movement.data.model.{ Geoposition, Sensor }
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{ DateTime, Instant }

object Data {

  private val fillASensor = createSensor("IDA", "Choppy") _
  private val fillBSensor = createSensor("IDB", "Hugger") _

  private val rawASensorTrack = Seq(
    fillASensor("2021-09-01T00:01:00.000Z", 64.155146, 115.917658, Direction.N),
    fillASensor("2021-09-01T00:01:05.000Z", 64.167866, 115.916566, Direction.N),
    fillASensor("2021-09-01T00:01:10.000Z", 64.174548, 115.915173, Direction.N),
    fillASensor("2021-09-01T00:01:15.000Z", 64.198284, 115.842016, Direction.NW),
    fillASensor("2021-09-01T00:01:20.000Z", 64.202706, 115.832502, Direction.NW),
    fillASensor("2021-09-01T00:01:25.000Z", 64.202706, 115.832502, Direction.NW),
    fillASensor("2021-09-01T00:01:30.000Z", 64.202706, 115.832502, Direction.NW),
    fillASensor("2021-09-01T00:01:35.000Z", 64.202706, 115.832502, Direction.NW),
    fillASensor("2021-09-01T00:01:40.000Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:01:45.000Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:01:50.000Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:01:55.000Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:00.000Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:05.000Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:10.000Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:15.000Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:20.000Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:25.000Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:30.000Z", 64.203855, 115.828833, Direction.NW),
    fillASensor("2021-09-01T00:02:35.000Z", 64.203855, 115.828833, Direction.NW)
  )

  private val rawBSensorTrack = Seq(
    fillBSensor("2021-09-01T00:01:01.000Z", 64.155146, 115.917658, Direction.N),
    fillBSensor("2021-09-01T00:01:06.000Z", 64.167866, 115.916566, Direction.N),
    fillBSensor("2021-09-01T00:01:11.000Z", 64.174548, 115.915173, Direction.N),
    fillBSensor("2021-09-01T00:01:16.000Z", 64.198284, 115.842016, Direction.NW),
    fillBSensor("2021-09-01T00:01:21.000Z", 64.202706, 115.832502, Direction.NW),
    fillBSensor("2021-09-01T00:01:26.000Z", 64.202706, 115.832502, Direction.NW),
    fillBSensor("2021-09-01T00:01:31.000Z", 64.202706, 115.832502, Direction.NW),
    fillBSensor("2021-09-01T00:01:36.000Z", 64.202706, 115.832502, Direction.NW),
    fillBSensor("2021-09-01T00:01:41.000Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:01:46.000Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:01:51.000Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:01:56.000Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:01.000Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:06.000Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:11.000Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:16.000Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:21.000Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:26.000Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:31.000Z", 64.203855, 115.828833, Direction.NW),
    fillBSensor("2021-09-01T00:02:36.000Z", 64.203855, 115.828833, Direction.NW)
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
    val dateTime    = DateTime.parse(dateTimeStr, ISODateTimeFormat.dateTime)
    val metrics     = Metrics(dateTime.getMillis, geoposition)

    Sensor(sensorId, carrier, metrics)
  }

}
