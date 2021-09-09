package dope.nathan.movement.data.collector

import logic.CollectorLogic

import cloudflow.spark.{SparkStreamlet, SparkStreamletLogic}
import cloudflow.streamlets.StreamletShape
import cloudflow.streamlets.avro.AvroInlet
import dope.nathan.movement.data.model.event.TrackMade

trait CollectorShape extends SparkStreamlet {
  val trackMadeIn: AvroInlet[TrackMade] =
    AvroInlet("track-made-in")

  override def shape(): StreamletShape =
    StreamletShape.withInlets(trackMadeIn)
}

trait CollectorBase extends CollectorShape {
  override protected def createLogic(): SparkStreamletLogic =
    CollectorLogic.apply(trackMadeIn)
}

object Collector extends CollectorBase {
  System.setProperty("hadoop.home.dir", "C:\\Spark\\spark-2.4.5-bin-hadoop2.7") // to run in the sandbox
}
