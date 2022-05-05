/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.model

import io.circe._
import io.circe.generic.semiauto._

final case class Schema(`type`: String, format: Option[String])
object Schema {
  implicit lazy val encoder: Encoder[Schema] = deriveEncoder

  val int32Integer   = Schema(SchemaType.INTEGER, SchemaFormat.INT32)
  val int64Integer   = Schema(SchemaType.INTEGER, SchemaFormat.INT64)
  val floatNumber    = Schema(SchemaType.NUMBER, SchemaFormat.FLOAT)
  val doubleNumber   = Schema(SchemaType.NUMBER, SchemaFormat.DOUBLE)
  val boolean        = Schema(SchemaType.BOOLEAN, None)
  val string         = Schema(SchemaType.STRING, None)
  val byteString     = Schema(SchemaType.STRING, SchemaFormat.BYTE)
  val binaryString   = Schema(SchemaType.STRING, SchemaFormat.BINARY)
  val dateString     = Schema(SchemaType.STRING, SchemaFormat.DATE)
  val dateTimeString = Schema(SchemaType.STRING, SchemaFormat.DATETIME)
  val emailString    = Schema(SchemaType.STRING, SchemaFormat.EMAIL)
  val passwordString = Schema(SchemaType.STRING, SchemaFormat.PASSWORD)
  val uuidString     = Schema(SchemaType.STRING, SchemaFormat.UUID)

  object SchemaType {
    val BOOLEAN = "boolean"
    val OBJECT  = "object"
    val ARRAY   = "array"
    val NUMBER  = "number"
    val STRING  = "string"
    val INTEGER = "integer"
  }

  object SchemaFormat {
    val INT32:    Option[String] = Some("int32")
    val INT64:    Option[String] = Some("int64")
    val FLOAT:    Option[String] = Some("float")
    val DOUBLE:   Option[String] = Some("double")
    val BYTE:     Option[String] = Some("byte")
    val BINARY:   Option[String] = Some("binary")
    val DATE:     Option[String] = Some("date")
    val DATETIME: Option[String] = Some("date-time")
    val EMAIL:    Option[String] = Some("email")
    val PASSWORD: Option[String] = Some("password")
    val UUID:     Option[String] = Some("uuid")
  }
}
