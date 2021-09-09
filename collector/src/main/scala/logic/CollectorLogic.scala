package dope.nathan.movement.data.collector
package logic

import logic.SparkEncoderEnrichment._

import cloudflow.spark.{SparkStreamletContext, SparkStreamletLogic, StreamletQueryExecution}
import cloudflow.streamlets.avro.AvroInlet
import dope.nathan.movement.data.common.auxiliary.{BaseLogging, ExceptionManagement, ProcessLogging}
import dope.nathan.movement.data.model.event.TrackMade
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.streaming.{OutputMode, StreamingQuery}

import scala.util.control.Exception.{catching, nonFatalCatcher}

case class CollectorLogic(trackMadeIn: AvroInlet[TrackMade])(implicit override val context: SparkStreamletContext)
    extends SparkStreamletLogic
    with BaseLogging
    with ProcessLogging[TrackMade]
    with ExceptionManagement {

  override def buildStreamingQueries: StreamletQueryExecution =
    catching(nonFatalCatcher).either {
      val trackMadeSet = readStream(trackMadeIn)
      makeStreamingQuery(trackMadeSet).toQueryExecution

    }.fold(castAndThrow("Could not build the queries"), identity)

  private def makeStreamingQuery(trackMadeSet: Dataset[TrackMade]): StreamingQuery = {
    val track = trackMadeSet
      .map(log("Start", _, logger.debug))
      .map(_.track)

    val trackWriter = track.writeStream
      .format("console")
      .outputMode(OutputMode.Append())

    trackWriter.start()
  }

}
