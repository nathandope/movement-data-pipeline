package dope.nathan.movement.data.converter
package logic.enrichment.metrics

import dope.nathan.movement.data.model.geoposition.{Coordinates, Direction}
import dope.nathan.movement.data.model.track.TrackPoint

import scala.math._

private[metrics] trait MetricsCalculation {
  import MetricsCalculation._

  protected def calculateTimeFrame(firstTrackPoint: TrackPoint, lastTrackPoint: TrackPoint): (Long, Long) =
    (firstTrackPoint.timestamp, lastTrackPoint.timestamp)

  protected def calculateDuration(firstTrackPoint: TrackPoint, lastTrackPoint: TrackPoint): Long =
    lastTrackPoint.timestamp - firstTrackPoint.timestamp

  protected def calculateTrackTrigonometry(
    headTrackPoint: TrackPoint,
    lastTrackPoint: TrackPoint
  ): TrackTrigonometry = {
    val headPointCoordinates = headTrackPoint.geoposition.coordinates
    val lastPointCoordinates = lastTrackPoint.geoposition.coordinates

    val headPointCoordinatesInRadians = Coordinates(
      toRadians(headPointCoordinates.lat),
      toRadians(headPointCoordinates.lon)
    )

    val lastPointCoordinatesInRadians = Coordinates(
      toRadians(lastPointCoordinates.lat),
      toRadians(lastPointCoordinates.lon)
    )

    val theta = lastPointCoordinatesInRadians.lon - headPointCoordinatesInRadians.lon

    val headPointLatitudeSinCos = SinCos(
      sin(headPointCoordinatesInRadians.lat),
      cos(headPointCoordinatesInRadians.lat)
    )

    val lastPointLatitudeSinCos = SinCos(
      sin(lastPointCoordinatesInRadians.lat),
      cos(lastPointCoordinatesInRadians.lat)
    )

    val thetaSinCos = SinCos(
      sin(theta),
      cos(theta)
    )

    TrackTrigonometry(headPointLatitudeSinCos, lastPointLatitudeSinCos, thetaSinCos)
  }

  protected def calculateDistance(trackTrigonometry: TrackTrigonometry): Double = {
    import trackTrigonometry._

    val ordinateCoordinates = sqrt(
      pow(headPointLatitude.cos * theta.sin, 2) +
        pow(
          (headPointLatitude.cos * lastPointLatitude.sin) -
            (headPointLatitude.sin * lastPointLatitude.cos * theta.cos),
          2
        )
    )
    val abscissaCoordinates = {
      (headPointLatitude.sin * lastPointLatitude.sin) +
        (headPointLatitude.cos * lastPointLatitude.cos * theta.cos)
    }

    val polarCoordinates = atan2(ordinateCoordinates, abscissaCoordinates)

    polarCoordinates * EarthRadius
  }

  protected def calculateSpeed(distance: Double, duration: Long): Double = distance / duration

  protected def calculateDirection(
    headTrackPoint: TrackPoint,
    lastTrackPoint: TrackPoint,
    trackTrigonometry: => TrackTrigonometry
  ): Direction = {
    val headAndLastPointsCoordinatesAreSame = {
      headTrackPoint.geoposition.coordinates ==
        lastTrackPoint.geoposition.coordinates
    }

    if (headAndLastPointsCoordinatesAreSame) {
      lastTrackPoint.geoposition.direction
    } else {
      val azimuth = calculateAzimuth(trackTrigonometry)

      azimuth match {
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
  }

  private def calculateAzimuth(trackTrigonometry: TrackTrigonometry): Double = {
    import trackTrigonometry._

    val degrees = {
      val x = theta.sin * headPointLatitude.cos

      val y = {
        (lastPointLatitude.cos * headPointLatitude.sin) -
          (lastPointLatitude.sin * headPointLatitude.cos * theta.cos)
      }

      val z = toDegrees(atan2(x, y))

      if (x < 0) z + Degrees180 else z
    }

    val radian   = -toRadians((degrees + Degrees180) % Degrees360 - Degrees180)
    val angleRad = radian - ((2 * Pi) * floor(radian / (2 * Pi)))

    angleRad * Degrees180 / Pi
  }
}

private[metrics] object MetricsCalculation {

  case class SinCos(sin: Double, cos: Double)
  case class TrackTrigonometry(headPointLatitude: SinCos, lastPointLatitude: SinCos, theta: SinCos)

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
  private def nneDegrees: Double => Boolean   = prepareMatcherBySegmentNum(0)
  private def neDegrees: Double => Boolean    = prepareMatcherBySegmentNum(1)
  private def eneDegrees: Double => Boolean   = prepareMatcherBySegmentNum(2)
  private def eastDegrees: Double => Boolean  = prepareMatcherBySegmentNum(3)
  private def eseDegrees: Double => Boolean   = prepareMatcherBySegmentNum(4)
  private def seDegrees: Double => Boolean    = prepareMatcherBySegmentNum(5)
  private def sseDegrees: Double => Boolean   = prepareMatcherBySegmentNum(6)
  private def southDegrees: Double => Boolean = prepareMatcherBySegmentNum(7)
  private def sswDegrees: Double => Boolean   = prepareMatcherBySegmentNum(8)
  private def swDegrees: Double => Boolean    = prepareMatcherBySegmentNum(9)
  private def wswDegrees: Double => Boolean   = prepareMatcherBySegmentNum(10)
  private def westDegrees: Double => Boolean  = prepareMatcherBySegmentNum(11)
  private def wnwDegrees: Double => Boolean   = prepareMatcherBySegmentNum(12)
  private def nwDegrees: Double => Boolean    = prepareMatcherBySegmentNum(13)
  private def nnwDegrees: Double => Boolean   = prepareMatcherBySegmentNum(14)

  private[this] def prepareMatcherBySegmentNum: Int => Double => Boolean =
    segmentNum =>
      azimuth => {
        DegreesHalfStep + DegreesStep * segmentNum <= azimuth &&
          azimuth < DegreesHalfStep + DegreesStep * (segmentNum + 1)
      }
}
