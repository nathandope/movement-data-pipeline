package dope.nathan.movement.data.converter
package logic.conversion
import dope.nathan.movement.data.converter.logic.state.{StateKeyMaker, StateMinute, StateTimeFrameMarker}
import dope.nathan.movement.data.model.event.TrackMade
import dope.nathan.movement.data.model.sensor.Sensor
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.util.{KeyedOneInputStreamOperatorTestHarness => OperatorTestHarness, ProcessFunctionTestHarnesses => FuncTestHernesses}
import org.scalatest.{BeforeAndAfter, Matchers}

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class SensorToTrackSpec extends AnyWordSpecLike with Matchers with BeforeAndAfter {

  private val x1 = (30 seconds) toMillis
  private val x2 = (30 seconds) toMillis

  private var testHarness: OperatorTestHarness[String, Sensor, TrackMade] =
    createHarness(SensorToTrack(x1, x2), StateKeyMaker(_, StateMinute, StateTimeFrameMarker))

  private def createHarness(
    processFunction: SensorToTrack,
    keySelector: KeySelector[Sensor, String]
  ): OperatorTestHarness[String, Sensor, TrackMade] = {
    FuncTestHernesses
      .forKeyedProcessFunction[String, Sensor, TrackMade](
        processFunction,
        keySelector,
        createTypeInformation[String]
      )
  }

  before {
    testHarness = createHarness(SensorToTrack(x1, x2), _.id)
    testHarness.getExecutionConfig.setAutoWatermarkInterval((1 minute) toMillis)
    testHarness.open()
  }
}
