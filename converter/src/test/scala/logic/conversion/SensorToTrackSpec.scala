//package dope.nathan.movement.data.converter
//package logic.conversion
//import logic.state.track.{Minutely, TrackTimeBoundariesMarker}
//
//import dope.nathan.movement.data.model.event.TrackMade
//import dope.nathan.movement.data.model.sensor.Sensor
//import org.apache.flink.api.java.functions.KeySelector
//import org.apache.flink.api.scala.createTypeInformation
//import org.apache.flink.streaming.api.TimeCharacteristic
//import org.apache.flink.streaming.api.scala.OutputTag
//import org.apache.flink.streaming.util.{KeyedOneInputStreamOperatorTestHarness => OperatorTestHarness, ProcessFunctionTestHarnesses => FuncTestHernesses}
//import org.joda.time.Instant
//import org.scalatest.BeforeAndAfter
//import org.scalatest.matchers.should.Matchers
//import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
//import org.scalatest.wordspec.AnyWordSpecLike
//
//import scala.jdk.CollectionConverters.collectionAsScalaIterableConverter
//import scala.language.postfixOps
//
//class SensorToTrackSpec extends AnyWordSpecLike with Matchers with BeforeAndAfter {
//
//  private val stateAccumulationPeriod = Minutely
//  private val stateReleaseTimeout     = (5 seconds) toMillis
//  private val stateTimeToLive         = (30 seconds) toMillis
//
//  private val conversion    = SensorToTrack(stateReleaseTimeout, stateTimeToLive)
//  private val stateKeyMaker = StateKeyMaker(TrackTimeBoundariesMarker(stateAccumulationPeriod))
//  private var harness       = createHarness(conversion, stateKeyMaker)
//
//  private def createHarness(
//    processFunction: SensorToTrack,
//    keySelector: KeySelector[Sensor, String]
//  ): OperatorTestHarness[String, Sensor, TrackMade] =
//    FuncTestHernesses
//      .forKeyedProcessFunction[String, Sensor, TrackMade](
//        processFunction,
//        keySelector,
//        createTypeInformation[String]
//      )
//
//  before {
//    harness = createHarness(conversion, stateKeyMaker)
//    harness.setTimeCharacteristic(TimeCharacteristic.IngestionTime)
//
//    harness.open()
//  }
//
//  "1" should {
//    "11" in {
//      Data.rawSensorsTrack.tail.foreach { sensorData =>
//        val currentTime = Instant.now.getMillis
//        harness.processElement(sensorData, currentTime)
//
//        harness.setProcessingTime(currentTime + ((4 seconds) toMillis))
//      }
//
////      Data.rawSensorsTrack.headOption.foreach { headSensorData =>
////        val currentTime = Instant.now.getMillis
////        harness.processElement(headSensorData, currentTime )
////        harness.setProcessingTime(currentTime + ((16 seconds) toMillis))
////      }
//
//      harness.setProcessingTime(Instant.now.getMillis + ((16 seconds) toMillis))
//
//
//      harness.getOutput.size should be(4)
//    }
//  }
//}
