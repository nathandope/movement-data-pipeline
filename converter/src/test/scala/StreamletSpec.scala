package dope.nathan.movement.data.converter

import cloudflow.flink.testkit.{FlinkSource, FlinkTestkit, TestFlinkStreamletContext}
import com.typesafe.config.{Config, ConfigFactory}
import dope.nathan.movement.data.model.event.{SensorDataGot, TrackMade}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import scala.collection.JavaConverters._


class StreamletSpec extends FlinkTestkit with AnyWordSpecLike with Matchers with BeforeAndAfter {
  private val env = StreamExecutionEnvironment.getExecutionEnvironment

  override def config: Config = ConfigFactory.parseMap(TestConfig.configMap.asJava)

  "1" should {
    "11" in {
      val inputEventStream = env.addSource(FlinkSource.CollectionSourceFunction(Data.events))

      val sensorDataGotIn = inletAsTap[SensorDataGot](Convertor.sensorIn, inputEventStream)
      val trackMadeOut = outletAsTap[TrackMade](Convertor.trackOut)

      run(Convertor, Seq(sensorDataGotIn), Seq(trackMadeOut), env)

      val result = TestFlinkStreamletContext.result

      println(result.toArray.mkString(", "))
    }
  }
}
