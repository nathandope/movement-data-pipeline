package dope.nathan.movement.data.collector

import logic.CollectorLogic

import cloudflow.spark.{SparkStreamlet, SparkStreamletLogic}
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroInlet
import dope.nathan.movement.data.model.event.TrackMade

trait CollectorOpenings {
  val trackMadeIn: AvroInlet[TrackMade] =
    AvroInlet("track-made-in")
}

trait CollectorBase extends SparkStreamlet with CollectorOpenings {
  override def shape(): StreamletShape =
    StreamletShape.withInlets(trackMadeIn)

  override protected def createLogic(): SparkStreamletLogic =
    new CollectorLogic
}

object Collector extends CollectorBase {
  System.setProperty("hadoop.home.dir", "C:\\Spark\\spark-2.4.5-bin-hadoop2.7") // to run in the sandbox
}
