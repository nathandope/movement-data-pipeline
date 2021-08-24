package dope.nathan.movement.data.converter
package logic.state.track

import org.joda.time.DateTime

trait TimeBoundariesMarkerBase[A, B] { def mark(a: A): B }

sealed trait TimeBoundariesMarker extends TimeBoundariesMarkerBase[DateTime, (DateTime, DateTime)] {

  def mark(dateTime: DateTime): (DateTime, DateTime) = {
    val leftTimeBoundary  = markLeftBoundary(dateTime)
    val rightTimeBoundary = markRightBoundary(leftTimeBoundary)

    (leftTimeBoundary, rightTimeBoundary)
  }

  def markLeftBoundary: DateTime => DateTime
  def markRightBoundary: DateTime => DateTime

}

case object MinutelyBoundariesMarker extends TimeBoundariesMarker {
  override def markLeftBoundary: DateTime => DateTime =
    _.withSecondOfMinute(0)
      .withMillisOfSecond(0)

  override def markRightBoundary: DateTime => DateTime =
    markLeftBoundary(_)
      .plusSeconds(59)
      .plusMillis(999)
}

case object HourlyBoundariesMarker extends TimeBoundariesMarker {
  override def markLeftBoundary: DateTime => DateTime =
    MinutelyBoundariesMarker
      .markLeftBoundary(_)
      .withMinuteOfHour(0)

  override def markRightBoundary: DateTime => DateTime =
    markLeftBoundary(_)
      .plusMinutes(59)
}

case object DailyBoundariesMarker extends TimeBoundariesMarker {
  override def markLeftBoundary: DateTime => DateTime =
    HourlyBoundariesMarker
      .markLeftBoundary(_)
      .withHourOfDay(0)

  override def markRightBoundary: DateTime => DateTime =
    markLeftBoundary(_)
      .plusHours(23)
}
