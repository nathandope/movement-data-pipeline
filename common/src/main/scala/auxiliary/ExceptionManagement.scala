package dope.nathan.movement.data.common
package auxiliary

trait ExceptionManagement {
  protected def castAndThrow(msg: String): Throwable => Nothing = t => {
    throw new IllegalArgumentException(msg, t)
  }
}
