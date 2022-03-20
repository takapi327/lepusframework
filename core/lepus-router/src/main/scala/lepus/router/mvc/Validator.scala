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
   */
  case class Pattern(value: String) extends Validator {
    def apply(t: String): Option[DecodeResult.Failure] = {
      if (t.matches(value)) None else Some(DecodeResult.InvalidValue(s"$t did not match the regular expression in $value", None))
    }
  }

  /**
   * Checks if the path parameter is below the specified threshold
   *
   * @param value threshold
   */
  case class Min(value: Int) extends Validator {
    def apply(t: String): Option[DecodeResult.Failure] = {
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
   */
  case class Max(value: Int) extends Validator {
    def apply(t: String): Option[DecodeResult.Failure] = {
      try if (t.toInt < value) None else Some(DecodeResult.InvalidValue(s"$t has exceeded the threshold value of $value", None))
      catch {
        case e: Throwable => Some(DecodeResult.InvalidValue(e.getMessage, Some(e)))
      }
    }
  }

  /**
   * Checks if the path parameter is in the specified range of values
   *
   * @param min Minimum threshold Value
   * @param max Maximum threshold value
   */
  case class Range(min: Int, max: Int) extends Validator {
    require(min < max, "The max argument must be greater than the min argument to be passed to the Range model.")
    def apply(t: String): Option[DecodeResult.Failure] = {
      try if (min <= t.toInt && t.toInt <= max) None else Some(DecodeResult.InvalidValue(s"$t is not in the $min and $max range", None))
      catch {
        case e: Throwable => Some(DecodeResult.InvalidValue(e.getMessage, Some(e)))
      }
    }
  }
}
