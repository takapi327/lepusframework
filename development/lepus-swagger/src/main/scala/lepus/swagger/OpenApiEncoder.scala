/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import scala.collection.immutable.ListMap

import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps

import lepus.router.model.Tag
import lepus.swagger.model._

trait OpenApiEncoder {
  implicit val encoderSchemaType: Encoder[OpenApiSchema.SchemaType] = { e => Encoder.encodeString(e.value) }
  implicit val encoderSchema:     Encoder[OpenApiSchema]            = deriveEncoder[OpenApiSchema]

  implicit lazy val parameterEncoder: Encoder[Parameter] = deriveEncoder
  implicit lazy val pathEncoder:      Encoder[Path]      = deriveEncoder

  implicit lazy val headerEncoder:   Encoder[Response.Header]  = deriveEncoder
  implicit lazy val contentEncoder:  Encoder[Response.Content] = deriveEncoder
  implicit lazy val responseEncoder: Encoder[Response]         = deriveEncoder

  implicit lazy val infoEncoder:      Encoder[Info]      = deriveEncoder
  implicit lazy val contactEncoder:   Encoder[Contact]   = deriveEncoder
  implicit lazy val licenseEncoder:   Encoder[License]   = deriveEncoder
  implicit lazy val serverEncoder:    Encoder[Server]    = deriveEncoder
  implicit lazy val componentEncoder: Encoder[Component] = deriveEncoder

  implicit lazy val tagEncoder: Encoder[Tag] = Encoder.instance { tag =>
    Json.obj(
      "name"        -> tag.name.asJson,
      "description" -> tag.description.asJson,
      "externalDocs" -> (for {
        desc <- tag.externalDocsDescription
        url  <- tag.externalDocsUrl
      } yield Json.obj(
        "description" -> desc.asJson,
        "url"         -> url.asJson
      )).asJson
    )
  }
  implicit lazy val swaggerUIEncoder: Encoder[SwaggerUI] = deriveEncoder

  implicit def encoderEither[T: Encoder]: Encoder[Either[Reference, T]] = {
    case Left(_)  => Json.obj(("$ref", Json.fromString("component/schemas")))
    case Right(t) => implicitly[Encoder[T]].apply(t)
  }

  implicit def encodeList[T: Encoder]: Encoder[List[T]] = {
    case Nil           => Json.Null
    case list: List[T] => Json.arr(list.map(i => implicitly[Encoder[T]].apply(i)): _*)
  }

  implicit def encodeListMap[V: Encoder]: Encoder[ListMap[String, V]] = encodeListMap(nullWhenEmpty = true)

  private def encodeListMap[V: Encoder](nullWhenEmpty: Boolean): Encoder[ListMap[String, V]] = {
    case m: ListMap[String, V] if m.isEmpty && nullWhenEmpty => Json.Null
    case m: ListMap[String, V] =>
      val properties = m.view.mapValues(v => implicitly[Encoder[V]].apply(v)).toList
      Json.obj(properties: _*)
  }
}
