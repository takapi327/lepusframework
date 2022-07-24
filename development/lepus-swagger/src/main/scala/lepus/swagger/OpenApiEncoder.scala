/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import scala.collection.immutable.ListMap

import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.syntax.EncoderOps

import lepus.router.model.Tag
import lepus.swagger.model.*

trait OpenApiEncoder:
  given Encoder[OpenApiSchema.SchemaType]   = { e => Encoder.encodeString(e.toString) }
  given Encoder[OpenApiSchema.SchemaFormat] = { e => Encoder.encodeString(e.toString) }
  given Encoder[OpenApiSchema]              = deriveEncoder[OpenApiSchema]

  given Encoder[Parameter.ParameterInType] =
    Encoder.instance { p => p.toString.asJson }

  given Encoder[Parameter] = deriveEncoder
  given Encoder[Path]      = deriveEncoder

  given Encoder[Content] = deriveEncoder

  given Encoder[RequestBody] = deriveEncoder

  given Encoder[Response.Header] = deriveEncoder
  given Encoder[Response]        = deriveEncoder

  given Encoder[Info]      = deriveEncoder
  given Encoder[Contact]   = deriveEncoder
  given Encoder[License]   = deriveEncoder
  given Encoder[Server]    = deriveEncoder
  given Encoder[Component] = deriveEncoder

  given Encoder[Tag] = Encoder.instance { tag =>
    Json.obj(
      "name"        -> tag.name.asJson,
      "description" -> tag.description.asJson,
      "externalDocs" -> (for
        desc <- tag.externalDocsDescription
        url  <- tag.externalDocsUrl
      yield Json.obj(
        "description" -> desc.asJson,
        "url"         -> url.asJson
      )).asJson
    )
  }
  given Encoder[OpenApiUI] = deriveEncoder

  given [T: Encoder]: Encoder[Either[Reference, T]] = {
    case Left(Reference(ref)) => Json.obj(("$ref", Json.fromString(ref)))
    case Right(t)             => summon[Encoder[T]].apply(t)
  }

  given[T: Encoder]: Encoder[List[T]] = {
    case Nil           => Json.Null
    case list: List[T] => Json.arr(list.map(i => summon[Encoder[T]].apply(i)): _*)
  }

  given[V: Encoder]: Encoder[ListMap[String, V]] = encodeListMap(nullWhenEmpty = true)

  private def encodeListMap[V: Encoder](nullWhenEmpty: Boolean): Encoder[ListMap[String, V]] = {
    case m: ListMap[String, V] if m.isEmpty && nullWhenEmpty => Json.Null
    case m: ListMap[String, V] =>
      val properties = m.view.mapValues(v => summon[Encoder[V]].apply(v)).toList
      Json.obj(properties: _*)
  }
