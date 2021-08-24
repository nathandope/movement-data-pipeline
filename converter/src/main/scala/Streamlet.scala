package dope.nathan.movement.data.converter

import logic.conversion.SensorToTrack
import logic.state.track.TrackTimeBoundariesMarker
import logic.state.{ StateConfig, StateKeyMaker }

import cloudflow.flink.{ FlinkStreamlet, FlinkStreamletLogic }
import cloudflow.streamlets.avro.{ AvroInlet, AvroOutlet }
import cloudflow.streamlets.{ ConfigParameter, StreamletShape }
import dope.nathan.movement.data.model.event.{ SensorDataGot, TrackMade }
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.windowing.time.Time

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
        context.env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)

        val stateConfig = StateConfig.apply
        val dataStream = readStream(sensorIn)
          .map(event => event.sensor)
          .keyBy(StateKeyMaker(TrackTimeBoundariesMarker(stateConfig.trackDurationForState)))
          .process(SensorToTrack(stateConfig.stateReleaseTimeout, stateConfig.stateTimeToLive))

        writeStream(trackOut, dataStream)
      }.left.foreach(log.error("Could not build a graph", _))
    }
  }
}

object Convertor extends ConvertorBase with ConvertorShape
