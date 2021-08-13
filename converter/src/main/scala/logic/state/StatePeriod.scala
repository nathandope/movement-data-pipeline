package dope.nathan.movement.data.converter
package logic.state

import scala.concurrent.duration.{ FiniteDuration, _ }
import scala.language.postfixOps

sealed trait StatePeriod extends Serializable { val value: FiniteDuration }
case object StateDay    extends StatePeriod { val value: FiniteDuration = 1 day    }
case object StateHour   extends StatePeriod { val value: FiniteDuration = 1 hour   }
case object StateMinute extends StatePeriod { val value: FiniteDuration = 1 minute }
