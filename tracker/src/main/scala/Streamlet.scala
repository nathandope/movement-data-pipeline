package dope.nathan.movement.data.tracker

import akka.NotUsed
import akka.stream.scaladsl._
import cloudflow.akkastream.scaladsl.RunnableGraphStreamletLogic
import cloudflow.akkastream.{ AkkaStreamlet, AkkaStreamletLogic }
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroOutlet
import dope.nathan.movement.data.model.Sensor

import scala.concurrent.duration._
import scala.util.Random

trait TrackerShape extends AkkaStreamlet {
  @transient val out: AvroOutlet[Sensor] = AvroOutlet("sensor-out")

  override def shape(): StreamletShape = StreamletShape.withOutlets(out)
}

trait TrackerBase extends TrackerShape {
  override protected def createLogic(): AkkaStreamletLogic = new RunnableGraphStreamletLogic() {
    val rate = RateConf.value
    println(s"Producing elements at $rate/s")
    def runnableGraph: RunnableGraph[NotUsed] = {
      val source = Source.repeat(NotUsed).map(_ => Sensor("id-" + Random.nextInt(9999), Random.nextDouble))
      source
        .throttle(rate, 1.seconds)
        .to(plainSink(out))
    }
  }

}

object Tracker extends TrackerBase
