/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.mvc

import org.specs2.mutable.Specification

object ValidatorTest extends Specification {

  "Testing the Validator" should {

    "Passing a number to a validation that only allows numbers will result in None." in {
      val validator = Validator.Pattern("^[0-9]+$")
      validator("123456").isEmpty
    }

    "Passing a non-numeric to a validation that allows only numbers will result in Some." in {
      val validator = Validator.Pattern("^[0-9]+$")
      validator("hogehoge").nonEmpty
    }

    "Passing a number greater than the minimum value to a validation with a minimum value will result in None." in {
      val validator = Validator.Min(0)
      validator("2").isEmpty
    }

    "If a validation with a minimum value is passed a number less than that value, it becomes Some." in {
      val validator = Validator.Min(5)
      validator("1").nonEmpty
    }

    "Passing a non-numeric value to a validation with a minimum value will result in Some." in {
      val validator = Validator.Min(5)
      validator("hogehoge").nonEmpty
    }

    "Passing a number greater than the maximum value to a validation with a minimum value will result in Some." in {
      val validator = Validator.Max(0)
      validator("2").nonEmpty
    }

    "If a validation with a maximum value is passed a number less than that value, it becomes None." in {
      val validator = Validator.Max(5)
      validator("1").isEmpty
    }

    "Passing a non-numeric value to a validation with a maximum value will result in Some." in {
      val validator = Validator.Max(5)
      validator("hogehoge").nonEmpty
    }

    "Passing a number within the range to a validation that specifies a range will result in None." in {
      val validator = Validator.Range(0, 5)
      validator("4").isEmpty
    }

    "Passing a number outside that range to a validation that specifies a range will result in a Some." in {
      val validator = Validator.Range(0, 5)
      validator("100").nonEmpty
    }

    "Passing a non-numeric value to a validation that specifies a range will result in Some." in {
      val validator = Validator.Range(0, 5)
      validator("hogehoge").nonEmpty
    }

    "When defining a Validator Range, if the value of the min argument is larger than the max argument, an IllegalArgumentException exception is raised." in {
      Validator.Range(5, 0) must throwAn[IllegalArgumentException]
    }
  }
}
