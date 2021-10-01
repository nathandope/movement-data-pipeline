package dope.nathan.movement.data.collector
package logic

import logic.SparkEncoderEnrichment._

import cloudflow.spark.{SparkStreamletContext, SparkStreamletLogic, StreamletQueryExecution}
import dope.nathan.movement.data.common.auxiliary.{Logging, ProcessLogging, ThrowableManagement}
import dope.nathan.movement.data.model.event.TrackMade
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.streaming.{OutputMode, StreamingQuery}

class CollectorLogic(implicit override val context: SparkStreamletContext)
    extends SparkStreamletLogic
    with CollectorOpenings
    with ThrowableManagement
    with Logging {

  override def buildStreamingQueries: StreamletQueryExecution = {
    val exceptionOrQueryExecution = safely {
      val trackMadeSet = readStream(trackMadeIn)
      CollectorLogic.makeStreamingQuery(trackMadeSet).toQueryExecution
    }("Could not build the streaming queries")

    exceptionOrQueryExecution
      .fold(logAndThrow, identity)
  }

}

object CollectorLogic extends Logging with ProcessLogging {
  private def makeStreamingQuery(trackMadeSet: Dataset[TrackMade]): StreamingQuery = {
    val track = trackMadeSet
      .map(log("Start", logger.debug))
      .map(_.track)

    val trackWriter = track.writeStream
      .format("console")
      .outputMode(OutputMode.Append())

    trackWriter.start()
  }
}
