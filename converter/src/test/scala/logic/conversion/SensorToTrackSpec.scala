package dope.nathan.movement.data.converter
package logic.conversion
import logic.state.{StateKeyMaker, StateMinute, StateTimeFrameMarker}

import dope.nathan.movement.data.model.event.TrackMade
import dope.nathan.movement.data.model.sensor.Sensor
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.util.{KeyedOneInputStreamOperatorTestHarness => OperatorTestHarness, ProcessFunctionTestHarnesses => FuncTestHernesses}
import org.joda.time.Instant
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AnyWordSpecLike

import scala.language.postfixOps

class SensorToTrackSpec extends AnyWordSpecLike with Matchers with BeforeAndAfter {

  private val stateAccumulationPeriod = StateMinute
  private val stateReleaseTimeout     = (5 seconds) toMillis
  private val stateTimeToLive         = (60 seconds) toMillis

  private val conversion    = SensorToTrack(stateReleaseTimeout, stateTimeToLive)
  private val stateKeyMaker = new StateKeyMaker(stateAccumulationPeriod, StateTimeFrameMarker)
  private var harness       = createHarness(conversion, stateKeyMaker)

  private def createHarness(
    processFunction: SensorToTrack,
    keySelector: KeySelector[Sensor, String]
  ): OperatorTestHarness[String, Sensor, TrackMade] =
    FuncTestHernesses
      .forKeyedProcessFunction[String, Sensor, TrackMade](
        processFunction,
        keySelector,
        createTypeInformation[String]
      )

  before {
    harness = createHarness(conversion, stateKeyMaker)
//    harness.getExecutionConfig.setLatencyTrackingInterval((4 seconds) toMillis)
//    harness.getExecutionConfig.setExecutionMode(ExecutionMode.PIPELINED)
//    harness.getExecutionConfig.setTaskCancellationInterval((60 seconds)toMillis)
//    harness.getExecutionConfig.setTaskCancellationTimeout((60 seconds) toMillis)
    harness.setTimeCharacteristic(TimeCharacteristic.IngestionTime)
    harness.open()
  }

  "1" should {
    "11" in {
      Data.rowSensor1Track.foreach{ x =>
        val r = Instant.now.getMillis
        harness.processElement(x, r)
        harness.setProcessingTime(r + ((60 seconds) toMillis))
      }

      harness.getOutput should not be empty
    }
  }
}
