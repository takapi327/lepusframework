/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.mvc

import lepus.router.model.DecodeResult

sealed trait Validator {
  def apply(t: String): Option[DecodeResult.Failure]
}

object Validator {

  /**
   * Validation check with regular expression with path parameter
   *
   * @param value regular expression string
   * @tparam T Receives a String since it will be a string that will be the path parameter.
   */
  case class Pattern[T <: String](value: String) extends Validator {
    def apply(t: T): Option[DecodeResult.Failure] = {
      if (t.matches(value)) None else Some(DecodeResult.InvalidValue(s"$t did not match the regular expression in $value", None))
    }
  }

  /**
   * Checks if the path parameter is below the specified threshold
   *
   * @param value threshold
   * @tparam T Receives a String since it will be a string that will be the path parameter.
   */
  case class Min[T <: String](value: Int) extends Validator {
    def apply(t: T): Option[DecodeResult.Failure] = {
      try if (t.toInt > value) None else Some(DecodeResult.InvalidValue(s"$t has exceeded the threshold value of $value", None))
      catch {
        case e: Throwable => Some(DecodeResult.InvalidValue(e.getMessage, Some(e)))
      }
    }
  }

  /**
   * Checks if the path parameter exceeds the specified threshold
   *
   * @param value threshold
   * @tparam T Receives a String since it will be a string that will be the path parameter.
   */
  case class Max[T <: String](value: Int) extends Validator {
    def apply(t: T): Option[DecodeResult.Failure] = {
      try if (t.toInt < value) None else Some(DecodeResult.InvalidValue(s"$t has exceeded the threshold value of $value", None))
      catch {
        case e: Throwable => Some(DecodeResult.InvalidValue(e.getMessage, Some(e)))
      }
    }
  }

  /**
   * Checks if the path parameter is in the specified range of values
   *
   * @param min Minimum Threshold Value
   * @param max Maximum threshold value
   * @tparam T Receives a String since it will be a string that will be the path parameter.
   */
  case class Range[T <: String](min: Int, max: Int) extends Validator {
    def apply(t: T): Option[DecodeResult.Failure] = {
      try if (min < t.toInt && t.toInt < max) None else Some(DecodeResult.InvalidValue(s"$t is not in the $min and $max range", None))
      catch {
        case e: Throwable => Some(DecodeResult.InvalidValue(e.getMessage, Some(e)))
      }
    }
  }
}
