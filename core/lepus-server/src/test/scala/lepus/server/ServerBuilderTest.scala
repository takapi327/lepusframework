/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.server

import scala.concurrent.duration.*

import org.specs2.mutable.Specification

import com.google.inject.Injector

import cats.effect.{ IO, Resource }

import org.http4s.server.Server

import lepus.app.LepusApp

object ServerBuilderTest extends Specification, ServerBuilder[IO]:

  def buildServer(app: LepusApp[IO]): Injector ?=> Resource[IO, Server] = null

  "Testing the Naming ServerBuilder" should {
    "The value of port retrieved from conf matches the specified value" in {
      port.nonEmpty and port.contains(5555)
    }

    "The value of hostIPv4 retrieved from conf matches the specified value" in {
      hostIPv4.nonEmpty and hostIPv4.contains("127.0.0.1")
    }

    "The value of hostIPv6 retrieved from conf matches the specified value" in {
      hostIPv6.nonEmpty and hostIPv6.contains("0:0:0:0:0:0:0:1")
    }

    "The value of maxConnections retrieved from conf matches the specified value" in {
      maxConnections.nonEmpty and maxConnections.contains(1024)
    }

    "The value of receiveBufferSize retrieved from conf matches the specified value" in {
      receiveBufferSize.nonEmpty and receiveBufferSize.contains(256 * 1024)
    }

    "The value of maxHeaderSize retrieved from conf matches the specified value" in {
      maxHeaderSize.nonEmpty and maxHeaderSize.contains(40 * 1024)
    }

    "The value of requestHeaderReceiveTimeout retrieved from conf matches the specified value" in {
      requestHeaderReceiveTimeout.nonEmpty and requestHeaderReceiveTimeout.contains(5.seconds)
    }

    "The value of idleTimeout retrieved from conf matches the specified value" in {
      idleTimeout.nonEmpty and idleTimeout.contains(60.seconds)
    }

    "The value of shutdownTimeout retrieved from conf matches the specified value" in {
      shutdownTimeout.nonEmpty and shutdownTimeout.contains(30.seconds)
    }
  }
