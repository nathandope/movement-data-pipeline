package dope.nathan.movement.data.common
package auxiliary

import org.slf4j.{ Logger, LoggerFactory }

trait BaseLogging {
  lazy val logger: Logger = LoggerFactory.getLogger(getClass)
}

trait ProcessLogging {
  protected def log[T](stage: String, logFunc: => String => Unit): T => T = value => {
    val msg = s"""$stage processing:
           |${value.toString}
           |""".stripMargin
    logFunc(msg)

    value
  }
}
