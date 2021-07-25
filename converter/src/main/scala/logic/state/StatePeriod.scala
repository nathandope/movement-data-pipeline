package dope.nathan.movement.data.converter
package logic.state

import scala.concurrent.duration.{ FiniteDuration, _ }
import scala.language.postfixOps

sealed trait StatePeriod { val value: FiniteDuration }
object StateDay    extends StatePeriod { val value: FiniteDuration = 1 day    }
object StateHour   extends StatePeriod { val value: FiniteDuration = 1 hour   }
object StateMinute extends StatePeriod { val value: FiniteDuration = 1 minute }
