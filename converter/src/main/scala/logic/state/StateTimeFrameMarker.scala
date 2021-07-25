package dope.nathan.movement.data.converter
package logic.state

import org.joda.time.{ DateTime, Interval }

object StateTimeFrameMarker {

  def apply(sensorTimestamp: Long, statePeriod: StatePeriod): Interval = {
    val markTimeBoundaries = statePeriod match {
      case StateMinute => markMinuteBoundaries _
      case StateHour   => markHourBoundaries _
      case StateDay    => markDayBoundaries _
    }

    val (leftBoundary, rightBoundary) = markTimeBoundaries(new DateTime(sensorTimestamp))

    new Interval(leftBoundary, rightBoundary)
  }

  private def markMinuteBoundaries(dateTime: DateTime): (DateTime, DateTime) = {
    val leftTimeBoundary  = markMinuteLeftBoundary(dateTime)
    val rightTimeBoundary = markMinuteRightBoundary(leftTimeBoundary)

    (leftTimeBoundary, rightTimeBoundary)
  }

  private def markHourBoundaries(dateTime: DateTime): (DateTime, DateTime) = {
    val leftTimeBoundary  = markHourLeftBoundary(dateTime)
    val rightTimeBoundary = markHourRightBoundary(leftTimeBoundary)

    (leftTimeBoundary, rightTimeBoundary)
  }

  private def markDayBoundaries(dateTime: DateTime): (DateTime, DateTime) = {
    val leftTimeBoundary  = markDayLeftBoundary(dateTime)
    val rightTimeBoundary = markDayRightBoundary(leftTimeBoundary)

    (leftTimeBoundary, rightTimeBoundary)
  }

  private def markMinuteLeftBoundary: DateTime => DateTime =
    _.withSecondOfMinute(0)
      .withMillisOfSecond(0)

  private def markMinuteRightBoundary: DateTime => DateTime =
    markMinuteLeftBoundary(_)
      .plusSeconds(59)
      .plusMillis(999)

  private def markHourLeftBoundary: DateTime => DateTime =
    markMinuteLeftBoundary(_)
      .withMinuteOfHour(0)

  private def markHourRightBoundary: DateTime => DateTime =
    markHourLeftBoundary(_)
      .plusMinutes(59)

  private def markDayLeftBoundary: DateTime => DateTime =
    markHourLeftBoundary(_)
      .withHourOfDay(0)

  private def markDayRightBoundary: DateTime => DateTime =
    markDayLeftBoundary(_)
      .plusHours(23)
}
