package dope.nathan.movement.data.collector
package logic

import logic.SparkEncoderEnrichment._

import cloudflow.spark.{ SparkStreamletContext, SparkStreamletLogic, StreamletQueryExecution }
import dope.nathan.movement.data.common.auxiliary.{ BaseLogging, ExceptionManagement, ProcessLogging }
import dope.nathan.movement.data.model.event.TrackMade
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.streaming.{ OutputMode, StreamingQuery }

class CollectorLogic(implicit override val context: SparkStreamletContext)
    extends SparkStreamletLogic
    with CollectorOpenings
    with ExceptionManagement {

  import CollectorLogic._

  override def buildStreamingQueries: StreamletQueryExecution =
    safelyBuildStreamingQueries.fold(throw _, identity)

  private def safelyBuildStreamingQueries =
    safely {
      val trackMadeSet = readStream(trackMadeIn)
      makeStreamingQuery(trackMadeSet).toQueryExecution
    }(Some("Could not build the streaming queries"))

}

object CollectorLogic extends BaseLogging with ProcessLogging[TrackMade] {
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
