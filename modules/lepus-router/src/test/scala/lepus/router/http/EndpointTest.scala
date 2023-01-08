/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.http

import org.scalatest.flatspec.AnyFlatSpec

import lepus.router.{ *, given }
import lepus.router.http.Endpoint.*

class EndpointTest extends AnyFlatSpec:

  it should "generate endpoint" in {
    assertCompiles("""
      import lepus.router.{ *, given }
      import lepus.router.http.Endpoint.*

      val endpoint1: Endpoint[String] = "test1" / bindPath[String]("p1")
      val endpoint2: Endpoint[String] = bindPath[String]("p1") / "test2"
      val endpoint3: Endpoint[(String, String)] = endpoint1 ++ endpoint2
      val endpoint4: Endpoint[(String, String, Long)] = endpoint1 ++ endpoint2 / bindPath[Long]("p1")
    """.stripMargin)
  }

  it should "generate endpoint failure" in {
    assertDoesNotCompile("""
      import lepus.router.{ *, given }
      import lepus.router.http.Endpoint.*

      val endpoint1: Endpoint[Long] = "test1" / bindPath[String]("p1")
      val endpoint2: Endpoint[Long] = bindPath[String]("p1") / "test2"
      val endpoint3: Endpoint[(Long, Long)] = endpoint1 and endpoint2
      val endpoint4: Endpoint[(Long, Long, String)] = endpoint1 and endpoint2 / bindPath[Long]("p1")
    """.stripMargin)
  }

  it should "compile" in {
    assertCompiles("""
      import cats.effect.IO
      import org.http4s.dsl.io.*
      import lepus.router.{ *, given }

      bindPath[Long]("p1") / bindPath[String]("p2") ->> RouterConstructor.of {
        case GET => Ok("Hello")
      }
    """.stripMargin)
  }

  it should "compile failure" in {
    assertDoesNotCompile("""
      import cats.effect.IO
      import org.http4s.dsl.io.*
      import lepus.router.{ *, given }

      bindPath[Long]("p1") / bindPath[String]("p2") ->> RouterConstructor.of[IO, (String, String)] {
        case GET => Ok("Hello")
      }
    """.stripMargin)
  }

  it should "The Endpoint string will be the same as the specified string." in {
    val endpoint1: Endpoint[String] = "test1" / bindPath[String]("p1")
    endpoint1.formatString === "test1/%s"
  }

  it should "" in {
    val endpoint1: Endpoint[String] = "test1" / bindPath[String]("p1")
    val endpoint2: Endpoint[String] = bindPath[String]("p1") / "test2"
    val endpoint3: Endpoint[(String, String)] = endpoint1 ++ endpoint2

    println(endpoint3.toString.format("hello", "world"))

    endpoint3.formatString === "test1/%s/%s/test2"
  }
