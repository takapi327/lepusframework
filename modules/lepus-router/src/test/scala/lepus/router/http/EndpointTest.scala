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
    endpoint1.toFormatPath === "test1/%s"
  }

  it should "If multiple Endpoints are combined, the string will be the same as the specified value." in {
    val endpoint1: Endpoint[String]           = "test1" / bindPath[String]("p1")
    val endpoint2: Endpoint[String]           = bindPath[String]("p1") / "test2"
    val endpoint3: Endpoint[(String, String)] = endpoint1 ++ endpoint2

    endpoint3.toFormatPath === "test1/%s/%s/test2"
  }

  it should "Matches the string specified when the path is generated from Endpoint." in {
    val endpoint: Endpoint[String] = "test1" / bindPath[String]("p1")
    endpoint.toFormatPath.format("hoge") === "test1/hoge"
  }

  it should "Matches the string specified when a path is generated from multiple composited Endpoints." in {
    val endpoint1: Endpoint[String]         = "test1" / bindPath[String]("p1")
    val endpoint2: Endpoint[Long]           = bindPath[Long]("p1") / "test2"
    val endpoint3: Endpoint[(String, Long)] = endpoint1 ++ endpoint2

    endpoint3.toFormatPath.format("hoge", 1L) === "test1/hoge/1/test2"
  }

  it should "The Endpoint string in the Query parameter will be the same as the specified string." in {
    val endpoint: Endpoint[(Int, Long)] = bindQuery[Int]("page") +& bindQuery[Long]("limit")
    endpoint.toFormatQuery.format(1, 10L) === "page=1&limit=10"
  }

  it should "The Endpoint string consisting of multiple Query parameters will be the same as the specified string." in {
    val endpoint1: Endpoint[List[String]]              = bindQuery[List[String]]("area")
    val endpoint2: Endpoint[(Int, Long)]               = bindQuery[Int]("page") +& bindQuery[Long]("limit")
    val endpoint3: Endpoint[(List[String], Int, Long)] = endpoint1 ++ endpoint2
    endpoint3.toFormatQuery.format("tokyo,kanagawa", 1, 10L) === "area=tokyo,kanagawa&page=1&limit=10"
  }

  it should "The string of the Endpoint from which the Path and Query are composited will be the same as the specified value." in {
    val endpoint1: Endpoint[String]              = "hello" / bindPath[String]("p1")
    val endpoint2: Endpoint[(Int, Long)]         = bindQuery[Int]("page") +& bindQuery[Long]("limit")
    val endpoint3: Endpoint[(String, Int, Long)] = endpoint1 ++ endpoint2

    endpoint3.formatString.format("world", 1, 10L) === "hello/world?page=1&limit=10"
  }
