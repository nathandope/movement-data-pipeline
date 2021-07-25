package dope.nathan.movement.data.converter

import logic.state.{StateConfig, StateKeyMaker, StateTimeFrameMarker}
import logic.conversion.SensorToTrack

import cloudflow.flink.{FlinkStreamlet, FlinkStreamletLogic}
import cloudflow.streamlets.avro.{AvroInlet, AvroOutlet}
import cloudflow.streamlets.{ConfigParameter, StreamletShape}
import dope.nathan.movement.data.model.event.{SensorDataGot, TrackMade}
import org.apache.flink.api.scala.createTypeInformation

trait ConvertorShape extends FlinkStreamlet {
  @transient val sensorIn: AvroInlet[SensorDataGot] = AvroInlet("sensor-in")
  @transient val trackOut: AvroOutlet[TrackMade]    = AvroOutlet("track-out")

  override def shape(): StreamletShape = StreamletShape(sensorIn).withOutlets(trackOut)
}

trait ConvertorBase extends ConvertorShape {

  override def configParameters: Vector[ConfigParameter] = StateConfig.allParameters

  override protected def createLogic(): FlinkStreamletLogic = new FlinkStreamletLogic {
    override def buildExecutionGraph(): Unit = {
      import scala.util.control.Exception._
      catching(nonFatalCatcher).either {
        val stateConfig = StateConfig.apply
        val dataStream = readStream(sensorIn)
          .map(_.sensor)
          .keyBy(sensor => StateKeyMaker(sensor, stateConfig.accumulationPeriod, StateTimeFrameMarker))
          .process(SensorToTrack(stateConfig.releaseTimeout, stateConfig.stateTimeToLive))

        writeStream(trackOut, dataStream)
      }.left
    }
  }
}

object Convertor extends ConvertorBase with ConvertorShape
