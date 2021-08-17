package dope.nathan.movement.data.converter
package logic.state.track

import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps

sealed trait TrackDuration { val value: FiniteDuration }
case object Daily    extends TrackDuration { val value: FiniteDuration = 1 day    }
case object Hourly   extends TrackDuration { val value: FiniteDuration = 1 hour   }
case object Minutely extends TrackDuration { val value: FiniteDuration = 1 minute }
