package dope.nathan.movement.data.common
package auxiliary

import scala.util.control.Exception.{ catching, nonFatalCatcher }

trait ExceptionManagement {
  protected def safely[A](doIt: A)(errorMsg: Option[String] = None): Either[IllegalArgumentException, A] =
    catching(nonFatalCatcher)
      .either(doIt)
      .fold(t => Left(cast(errorMsg, t)), Right(_))

  private def cast(errorMsg: Option[String], throwable: Throwable): IllegalArgumentException =
    new IllegalArgumentException(errorMsg.getOrElse(throwable.getMessage), throwable)
}
