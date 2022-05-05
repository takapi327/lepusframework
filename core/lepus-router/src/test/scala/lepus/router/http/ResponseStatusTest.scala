/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import org.specs2.mutable.Specification

import org.http4s.{ Status => Http4sStatus }

object ResponseStatusTest extends Specification {

  "Testing the ResponseStatus" should {

    "Ok in ResponseStatus is a successful response" in {
      ResponseStatus.Ok.isSuccess()
    }

    "Ok in ResponseStatus is not a response to an error" in {
      !ResponseStatus.Ok.isServerError()
    }

    "The toHttp4sStatus method can be used to convert to http4s Status" in {
      ResponseStatus.Ok.toHttp4sStatus() must beAnInstanceOf[Http4sStatus]
    }

    "IllegalArgumentException exception is thrown when the toHttp4sStatus method is executed if the http response code has an invalid value" in {
      ResponseStatus(1).toHttp4sStatus() must throwA[IllegalArgumentException]
    }

    "The specified error message will be displayed when IllegalArgumentException exception occurs" in {
      ResponseStatus(1000).toHttp4sStatus() must throwA[IllegalArgumentException]("Invalid status code: 1000")
    }
  }
}
