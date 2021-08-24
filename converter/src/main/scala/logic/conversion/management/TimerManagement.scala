package dope.nathan.movement.data.converter
package logic.conversion.management

import logic.conversion.SensorToTrack.FuncContext

import org.joda.time.DateTime
import org.slf4j.Logger

trait TimerManagement {

  implicit val log: Logger

  protected implicit class TimerServiceSyntax(ctx: FuncContext) {
    private val timerService = ctx.timerService

    def setTimer(lastModified: Long, timeout: Long): Long = {
      val time = lastModified + timeout
      timerService.registerProcessingTimeTimer(time)
      log.debug(s"Timer set. Fired time= ${new DateTime(time)} .")

      time
    }

    def removeTimer(lastModified: Long, timeout: Long): Long = {
      val time = lastModified + timeout
      timerService.deleteProcessingTimeTimer(time)
      log.debug(s"Timer removed. Fired time= ${new DateTime(time)} .")

      time
    }

  }

  protected def timerIsFired(timerTimestamp: Long, lastStateModified: Long, timerTimeout: Long): Boolean =
    timerTimestamp == lastStateModified + timerTimeout

}
