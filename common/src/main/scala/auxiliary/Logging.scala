package dope.nathan.movement.data.common
package auxiliary

import org.slf4j.{Logger, LoggerFactory}

trait BaseLogging {
  lazy val logger: Logger = LoggerFactory.getLogger(getClass)
}

trait ProcessLogging[T] {
  protected def log(stage: String, value: T, logFunc: => String => Unit): T = {
    val msg = s"""$stage processing:
           |${value.toString}
           |""".stripMargin
    logFunc(msg)

    value
  }
}

