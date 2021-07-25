package dope.nathan.movement.data.converter
package logic.conversion.management

import logic.conversion.SensorToTrack.FuncContext

trait TimerManagement {

  protected implicit class TimerServiceSyntax(ctx: FuncContext) {

    def setTimer(lastModified: Long, timeout: Long): Long = {
      val time = lastModified + timeout
      ctx.timerService.registerProcessingTimeTimer(time)
      time
    }

    def removeTimer(lastModified: Long, timeout: Long): Long = {
      val time = lastModified + timeout
      ctx.timerService.deleteProcessingTimeTimer(time)
      time
    }

  }

  protected def timerIsFired(timerTimestamp: Long, lastStateModified: Long, timerTimeout: Long): Boolean =
    timerTimestamp == lastStateModified + timerTimeout

}
