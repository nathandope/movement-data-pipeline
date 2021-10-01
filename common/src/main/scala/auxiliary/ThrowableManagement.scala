package dope.nathan.movement.data.common
package auxiliary

import scala.util.control.Exception.{catching, nonFatalCatcher}

trait ThrowableManagement extends LoggingBase {

  case class Error(msg: String, t: Throwable)

  protected def safely[A](doIt: => A)(errorMsg: String): Either[Error, A] =
    catching(nonFatalCatcher)
      .either(doIt)
      .fold(t => Left(Error(errorMsg, t)), Right(_))

  protected def logAndThrow: Error => Nothing = e => {
    logger.error(e.msg)
    throw e.t
  }

}
