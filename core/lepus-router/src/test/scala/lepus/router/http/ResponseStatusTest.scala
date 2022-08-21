/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import org.specs2.mutable.Specification

import org.http4s.Status as Http4sStatus

object ResponseStatusTest extends Specification:

  "Testing the ResponseStatus" should {

    "Ok in ResponseStatus is a successful response" in {
      Status.Ok.isSuccess
    }

    "Ok in ResponseStatus is not a response to an error" in {
      !Status.Ok.isServerError
    }

    "The toHttp4sStatus method can be used to convert to http4s Status" in {
      Status.Ok.toHttp4sStatus() must beAnInstanceOf[Http4sStatus]
    }
  }
