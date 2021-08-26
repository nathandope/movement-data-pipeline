package dope.nathan.movement.data.converter
package logic.enrichment.metrics

import dope.nathan.movement.data.model.geoposition.{ Coordinates, Direction }
import dope.nathan.movement.data.model.track.TrackPoint

import scala.math._

private[metrics] trait MetricsCalculation {
  import MetricsCalculation._

  protected def calculateTimeFrame(firstTrackPoint: TrackPoint, lastTrackPoint: TrackPoint): (Long, Long) =
    (firstTrackPoint.timestamp, lastTrackPoint.timestamp)

  protected def calculateDuration(firstTrackPoint: TrackPoint, lastTrackPoint: TrackPoint): Long =
    lastTrackPoint.timestamp - firstTrackPoint.timestamp

  protected def calculateSinCos(firstTrackPoint: TrackPoint, lastTrackPoint: TrackPoint): (FirstLat, LastLat, Theta) = {
    val firstCoords = firstTrackPoint.geoposition.coordinates
    val lastCoords  = lastTrackPoint.geoposition.coordinates

    val firstRadians = Coordinates(toRadians(firstCoords.lat), toRadians(firstCoords.lon))
    val lastRadians  = Coordinates(toRadians(lastCoords.lat), toRadians(lastCoords.lon))
    val theta        = lastRadians.lon - firstRadians.lon

    val firstLatSinCos = FirstLat(sin(firstRadians.lat), cos(firstRadians.lat))
    val lastLatSinCos  = LastLat(sin(lastRadians.lat), cos(lastRadians.lat))
    val thetaSinCos    = Theta(sin(theta), cos(theta))

    (firstLatSinCos, lastLatSinCos, thetaSinCos)
  }

  protected def calculateDistance(firstLat: FirstLat, lastLat: LastLat, theta: Theta): Double = {
    val ordinateCoordinates = sqrt(
      pow(firstLat.cos * theta.sin, 2) +
        pow((firstLat.cos * lastLat.sin) - (firstLat.sin * lastLat.cos * theta.cos), 2)
    )
    val abscissaCoordinates = firstLat.sin * lastLat.sin + firstLat.cos * lastLat.cos * theta.cos

    val polarCoordinates = atan2(ordinateCoordinates, abscissaCoordinates)

    polarCoordinates * EarthRadius
  }

  protected def calculateSpeed(distance: Double, duration: Long): Double = distance / duration

  protected def calculateDirection(firstLat: FirstLat, lastLat: LastLat, theta: Theta): Direction = {

    val initialAzimuth = calculateInitialAzimuth(firstLat, lastLat, theta)

    initialAzimuth match {
      case a if northDegrees(a) => Direction.N
      case a if nneDegrees(a)   => Direction.NNE
      case a if neDegrees(a)    => Direction.NE
      case a if eneDegrees(a)   => Direction.ENE
      case a if eastDegrees(a)  => Direction.E
      case a if eseDegrees(a)   => Direction.ESE
      case a if seDegrees(a)    => Direction.SE
      case a if sseDegrees(a)   => Direction.SSE
      case a if southDegrees(a) => Direction.S
      case a if sswDegrees(a)   => Direction.SSW
      case a if swDegrees(a)    => Direction.SW
      case a if wswDegrees(a)   => Direction.WSW
      case a if westDegrees(a)  => Direction.W
      case a if wnwDegrees(a)   => Direction.WNW
      case a if nwDegrees(a)    => Direction.NW
      case a if nnwDegrees(a)   => Direction.NNW
      case _                    => Direction.Zero
    }
  }

  private def calculateInitialAzimuth(firstLat: FirstLat, lastLat: LastLat, theta: Theta): Double = {
    val degrees = {
      val x = theta.sin * lastLat.cos
      val y = firstLat.cos * lastLat.sin - firstLat.sin * lastLat.cos * theta.cos
      val z = toDegrees(atan(-x / y))

      if (x < 0) z + Degrees180 else z
    }

    val radian   = toRadians((degrees + Degrees180) % Degrees360 - Degrees180)
    val angleRad = radian - ((2 * Pi) * floor(radian / (2 * Pi)))

    angleRad * Degrees180 / Pi
  }
}

private[metrics] object MetricsCalculation {

  sealed trait SinCos { val sin, cos: Double }
  case class FirstLat(sin: Double, cos: Double) extends SinCos
  case class LastLat(sin: Double, cos: Double)  extends SinCos
  case class Theta(sin: Double, cos: Double)    extends SinCos

  private val EarthRadius     = 6372795
  private val DegreesZero     = 0d
  private val Degrees180      = 180d
  private val Degrees360      = 360d
  private val DegreesStep     = 22.5
  private val DegreesHalfStep = 11.25

  private def northDegrees: Double => Boolean = azimuth => {
    (Degrees360 - DegreesStep) <= azimuth && azimuth <= Degrees360 ||
      DegreesZero <= azimuth && azimuth < DegreesHalfStep
  }
  private def nneDegrees: Double => Boolean   = worldPart(0)
  private def neDegrees: Double => Boolean    = worldPart(1)
  private def eneDegrees: Double => Boolean   = worldPart(2)
  private def eastDegrees: Double => Boolean  = worldPart(3)
  private def eseDegrees: Double => Boolean   = worldPart(4)
  private def seDegrees: Double => Boolean    = worldPart(5)
  private def sseDegrees: Double => Boolean   = worldPart(6)
  private def southDegrees: Double => Boolean = worldPart(7)
  private def sswDegrees: Double => Boolean   = worldPart(8)
  private def swDegrees: Double => Boolean    = worldPart(9)
  private def wswDegrees: Double => Boolean   = worldPart(10)
  private def westDegrees: Double => Boolean  = worldPart(11)
  private def wnwDegrees: Double => Boolean   = worldPart(12)
  private def nwDegrees: Double => Boolean    = worldPart(13)
  private def nnwDegrees: Double => Boolean   = worldPart(14)

  private[this] def worldPart: Int => Double => Boolean =
    segmentNum =>
      azimuth => {
        DegreesHalfStep + DegreesStep * segmentNum <= azimuth &&
          azimuth < DegreesHalfStep + DegreesStep * (segmentNum + 1)
      }
}
