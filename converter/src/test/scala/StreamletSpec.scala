package dope.nathan.movement.data.converter

import cloudflow.flink.testkit.{FlinkSource, FlinkTestkit, TestFlinkStreamletContext}
import com.typesafe.config.{Config, ConfigFactory}
import dope.nathan.movement.data.common.auxiliary.BaseLogging
import dope.nathan.movement.data.model.event.{SensorDataGot, TrackMade}
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.collection.JavaConverters._

class StreamletSpec extends FlinkTestkit with AnyWordSpecLike with Matchers with BeforeAndAfter with BaseLogging {

  private val env      = StreamExecutionEnvironment.getExecutionEnvironment
  private val testData = new TestData

  override def config: Config = ConfigFactory.parseMap(TestStreamletConfig.configMap.asJava)

  "Streamlet" should {
    "process SensorDataGot events and create TrackMade events" in {
      val inputEventStream = env.addSource(FlinkSource.CollectionSourceFunction(testData.inputEvents))

      val sensorDataGotIn = inletAsTap[SensorDataGot](Converter.sensorDataGotIn, inputEventStream)
      val trackMadeOut    = outletAsTap[TrackMade](Converter.trackMadeOut)

      run(Converter, Seq(sensorDataGotIn), Seq(trackMadeOut), env)

      val result = TestFlinkStreamletContext.result

      result.asScala.foreach(logger.debug)

      result.size should be(4)
    }
  }
}
