package dope.nathan.movement.data.collector
package logic

import dope.nathan.movement.data.model.Track
import dope.nathan.movement.data.model.event.TrackMade

object SparkEncoderEnrichment {

  implicit def trackMadeEncoder: org.apache.spark.sql.Encoder[TrackMade] =
    org.apache.spark.sql.Encoders.kryo[TrackMade]

  implicit def trackEncoder: org.apache.spark.sql.Encoder[Track] =
    org.apache.spark.sql.Encoders.kryo[Track]

}
