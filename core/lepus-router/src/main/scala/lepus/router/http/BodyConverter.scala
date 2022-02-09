/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.router.http

import java.nio.charset.StandardCharsets._

import cats.data.Validated

import io.circe.{ Encoder, Decoder, ParsingFailure, DecodingFailure, CursorOp, Printer, Errors }
import io.circe.syntax._
import io.circe.parser.decodeAccumulating

import fs2._

import ConvertResult._

trait BodyConverter[T] {
  def decode(s: String): ConvertResult
  def encode(t: T):      ConvertResult
}

object BodyConverter {

  case class Json[T](
    private val _decode: String => ConvertResult
  )(_encode: T => ConvertResult) extends BodyConverter[T] {
    override def decode(s: String): ConvertResult = _decode(s)
    override def encode(t: T):      ConvertResult = _encode(t)
  }

  object Json {
    def toJson[T: Encoder: Decoder](s: String): ConvertResult = circeJson[T].decode(s)
    def toJson[T: Encoder: Decoder](t: T):      ConvertResult = circeJson[T].encode(t)
  }

  implicit def circeJson[T: Encoder: Decoder]: Json[T] =
    Json { s => decodeCirceJson(s) } { t => encodeCirceJson(t) }

  implicit def decodeCirceJson[T: Encoder: Decoder](s: String): ConvertResult =
    decodeAccumulating[T](s) match {
      case Validated.Valid(value) => JsValue(value)
      case Validated.Invalid(circeFailures) =>
        val jsonFailures = circeFailures.map {
          case ParsingFailure(message, _) => Error.JsonError(message, List.empty)
          case failure: DecodingFailure =>
            val path   = CursorOp.opsToPath(failure.history)
            val fields = path.split("\\.").toList.filter(_.nonEmpty)
            Error.JsonError(failure.message, fields)
        }
        Error(s, Error.JsonDecodeException(jsonFailures.toList, Errors(circeFailures)))
    }

  implicit def encodeCirceJson[T: Encoder: Decoder](t: T): ConvertResult =
    JsValue(Printer.noSpaces.print(t.asJson))
}

trait ConvertResult {
  def toStream(): Stream[Pure, Byte]
}

object ConvertResult {
  sealed trait Success extends ConvertResult
  sealed trait Failure extends ConvertResult

  case class JsValue[T](value: T) extends Success {
    override def toStream(): Stream[Pure, Byte] = {
      val bytes = value.toString.getBytes(UTF_8)
      Stream.chunk(Chunk.array(bytes))
    }
  }

  case class Error(message: String, throwable: Throwable) extends Failure {
    override def toStream(): Stream[Pure, Byte] = {
      val bytes = message.getBytes(UTF_8)
      Stream.chunk(Chunk.array(bytes))
    }
  }
  object Error {
    case class JsonDecodeException(errors: List[JsonError], underlying: Throwable)
      extends Exception(underlying.getMessage, underlying, true, false)
    case class JsonError(message: String, path: List[String])
  }
}
