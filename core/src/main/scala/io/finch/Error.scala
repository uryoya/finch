package io.finch

import scala.reflect.ClassTag
import io.finch.request.items._

/**
 * A basic error from a Finch application.
 */
trait Error extends Exception

object Error {

  def apply(message: String): Error = new Error {
    override def getMessage: String = message
  }

  /**
   * An exception that collects multiple request reader errors.
   *
   * @param errors the errors collected from various request readers
   */
  case class RequestErrors(errors: Seq[Throwable]) extends Error {
    override def getMessage: String =
      "One or more errors reading request: " + errors.map(_.getMessage).mkString("\n  ","\n  ","")
  }

  /**
   * An exception that indicates a required request item (''header'', ''param'', ''cookie'', ''body'') was missing in
   * the request.
   *
   * @param item the missing request item
   */
  case class NotPresent(item: RequestItem) extends Error {
    override def getMessage: String = s"Required ${item.description} not present in the request."
  }

  /**
   * An exception that indicates a broken [[[io.finch.request.ValidationRule ValidationRule]] on the request item.
   *
   * @param item the invalid request item
   * @param rule the rule description
   */
  case class NotValid(item: RequestItem, rule: String) extends Error {
    override def getMessage: String = s"Validation failed: ${item.description} $rule."
  }

  /**
   * An exception that indicates that a request item could be parsed.
   *
   * @param item the invalid request item
   * @param targetType the type the item should be converted into
   * @param cause the cause of the parsing error
   */
  case class NotParsed(item: RequestItem, targetType: ClassTag[_], cause: Throwable) extends Error {
    override def getMessage: String =
      s"${item.description} cannot be converted to ${targetType.runtimeClass.getSimpleName}: ${cause.getMessage}."
    override def getCause: Throwable = cause
  }
}
