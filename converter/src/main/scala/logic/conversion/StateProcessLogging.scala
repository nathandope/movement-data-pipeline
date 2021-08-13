package dope.nathan.movement.data.converter
package logic.conversion

import org.apache.flink.api.common.state.ValueState
import org.joda.time.DateTime

trait StateProcessLogging {

  protected def logStartProcessMsg[K](
    processName: String,
    stateKey: K,
    processingTimestamp: Long
  )(
    log: String => Unit
  )(implicit
    valueState: ValueState[_]
  ): Unit = {
    val msg =
      s"""State $processName...
         |Current state key= $stateKey .
         |Current timestamp= ${new DateTime(processingTimestamp)} .
         |Current state value= ${valueState.value} .
         |""".stripMargin

    log(msg)
  }

  protected def logEndProcessMsg[V](processName: String, value: V)(log: String => Unit): Unit = {
    val msg = {
      val (firstChar, rest) = processName.splitAt(1)

      s"""State $processName.
         |${firstChar.toUpperCase}$rest value= $value .
         |""".stripMargin
    }

    log(msg)
  }

}
